package dev.jwkim.jgv.services.user;

import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import dev.jwkim.jgv.entities.user.EmailTokenEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.exceptions.TransactionalException;
import dev.jwkim.jgv.mappers.user.EmailTokenMapper;
import dev.jwkim.jgv.mappers.user.UserMapper;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.results.reservation.ReservationResult;
import dev.jwkim.jgv.results.user.*;
import dev.jwkim.jgv.utils.CryptoUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final EmailTokenMapper emailTokenMapper;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;


    @Autowired
    public UserService(UserMapper userMapper, EmailTokenMapper emailTokenMapper, JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.userMapper = userMapper;
        this.emailTokenMapper = emailTokenMapper;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // region 회원가입 / 비밀번호 암호화
    @Transactional
    public Result register(HttpServletRequest request, UserEntity user) throws MessagingException {
        String idRegex = "^(?=.*[a-z])(?=.*\\d).{6,20}$";
        Pattern idPattern = Pattern.compile(idRegex);
        Matcher idMatcher = idPattern.matcher(user.getUsId());
        if (!idMatcher.matches()) {
            return RegisterResult.FAILURE_INVALID_ID;
        }

        String passwordRegex = "(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,100}$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        Matcher passwordMatcher = passwordPattern.matcher(user.getUsPw());
        if (!passwordMatcher.matches()) {
            return RegisterResult.FAILURE_INVALID_PASSWORD;
        }
        if (
                user == null ||
                        user.getUsName() == null ||
                        user.getUsName().isEmpty() || user.getUsName().length() < 2 || user.getUsName().length() > 15 ||
                        user.getUsId() == null || user.getUsId().isEmpty() || user.getUsId().length() < 2 || user.getUsId().length() > 20 ||
                        user.getUsPw() == null || user.getUsPw().isEmpty() || user.getUsPw().length() < 8 || user.getUsPw().length() > 100 ||
                        user.getUsBirth() == null ||
                        user.getUsContact() == null || user.getUsContact().isEmpty() || user.getUsContact().length() < 10 || user.getUsContact().length() > 13 ||
                        user.getUsEmail() == null || user.getUsEmail().isEmpty() || user.getUsEmail().length() < 8 || user.getUsEmail().length() > 50 ||
                        user.getUsGender() == null || user.getUsAddr() == null || user.getUsAddr().isEmpty()

        ) {

            return CommonResult.FAILURE;
        }
        if (this.userMapper.selectUserByEmail(user.getUsId()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_ID;
        }
        if (this.userMapper.selectUserByEmail(user.getUsEmail()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_EMAIL;
        }
        if (this.userMapper.selectUserByContact(user.getUsContact()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_CONTACT;
        }
        if (this.userMapper.selectUserByNickname(user.getUsNickName()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setUsId(user.getUsId());
        user.setUsPw(encoder.encode(user.getUsPw()));
        user.setUsName(user.getUsName());
        user.setUsNickName(user.getUsNickName());
        user.setUsBirth(user.getUsBirth());
        user.setUsGender(user.getUsGender());
        user.setUsEmail(user.getUsEmail());
        user.setUsContact(user.getUsContact());
        user.setUsAddr(user.getUsAddr());
        user.setUsCreatedAt(LocalDateTime.now());
        user.setUsUpdatedAt(LocalDateTime.now());
        user.setUsIsDeleted(false);
        user.setUsIsAdmin(false);
        user.setUsIsSuspended(false);
        user.setUsIsVerified(false);


        if (this.userMapper.insertUser(user) == 0) {
            throw new TransactionalException();
        }
        EmailTokenEntity emailToken = new EmailTokenEntity();
        emailToken.setEmEmail(user.getUsEmail());
        emailToken.setEmKey(CryptoUtils.hashSha512(String.format("%s%s%f%f",
                user.getUsEmail(),
                user.getUsPw(),
                Math.random(),
                Math.random()
        )));
        emailToken.setEmCreatedAt(LocalDateTime.now());
        emailToken.setEmExpiresAt(LocalDateTime.now().plusHours(24));
        emailToken.setEmUsed(false);

        if (this.emailTokenMapper.insertEmailToken(emailToken) == 0) {
            throw new TransactionalException();
        }
        String validationLink = String.format(
                "%s://%s:%d/user/validate-email-token?emEmail=%s&emKey=%s",
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                emailToken.getEmEmail(),
                emailToken.getEmKey());
        //org.thymeleaf Context
        Context context = new Context();
        context.setVariable("ValidationLink", validationLink);
        String mailText = this.templateEngine.process("email/validationLink", context);
        // DOCTYPE 에 넣어서 문자열로 처리하겠다

        MimeMessage mimeMassage = this.mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMassage);
        mimeMessageHelper.setFrom("nyeonjae94@gmail.com");
        mimeMessageHelper.setTo(emailToken.getEmEmail());
        mimeMessageHelper.setSubject("[JGV] 회원가입 인증 링크");
        mimeMessageHelper.setText(mailText, true);
        this.mailSender.send(mimeMassage);

        return CommonResult.SUCCESS;
    }
// endregion //

    //region 로그인
    public Result login(UserEntity user) {
        if (user == null ||
                user.getUsId() == null || user.getUsId().isEmpty() || user.getUsId().length() < 2 || user.getUsId().length() > 20 ||
                user.getUsPw() == null || user.getUsPw().isEmpty() || user.getUsPw().length() < 8 || user.getUsPw().length() > 100) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectUserById(user.getUsId());
        if (dbUser == null || dbUser.isUsIsDeleted()) {
            return CommonResult.FAILURE;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(user.getUsPw(), dbUser.getUsPw())) {
            return CommonResult.FAILURE;
        }
        if (!dbUser.isUsIsVerified()) {
            return LoginResult.FAILURE_NOT_VERIFIED;
        }
        if (dbUser.isUsIsSuspended()) {
            return LoginResult.FAILURE_SUSPENDED;
        }
        user.setUsNum(dbUser.getUsNum());
        user.setUsPw(dbUser.getUsPw());
        user.setUsName(dbUser.getUsName());
        user.setUsNickName(dbUser.getUsNickName());
        user.setUsBirth(dbUser.getUsBirth());
        user.setUsGender(dbUser.getUsGender());
        user.setUsEmail(dbUser.getUsEmail());
        user.setUsContact(dbUser.getUsContact());
        user.setUsAddr(dbUser.getUsAddr());
        user.setUsCreatedAt(dbUser.getUsCreatedAt());
        user.setUsUpdatedAt(dbUser.getUsUpdatedAt());
        user.setUsIsDeleted(dbUser.isUsIsDeleted());
        user.setUsIsAdmin(dbUser.isUsIsAdmin());
        user.setUsIsSuspended(dbUser.isUsIsSuspended());
        user.setUsIsVerified(dbUser.isUsIsVerified());

        return CommonResult.SUCCESS;
    }
    // endregion

    // region 이메일 토큰 발급
    public Result validateEmailToken(EmailTokenEntity emailToken) {
        if (emailToken == null ||
                emailToken.getEmEmail() == null || emailToken.getEmEmail().length() < 8 || emailToken.getEmEmail().length() > 50 ||
                emailToken.getEmKey() == null || emailToken.getEmKey().length() != 128) {
            return CommonResult.FAILURE;
        }
        EmailTokenEntity dbEmailToken = this.emailTokenMapper.selectEmailTokenByUserEmailAndKey
                (emailToken.getEmEmail(), emailToken.getEmKey());
        if (dbEmailToken == null || dbEmailToken.isEmUsed()) {
            return CommonResult.FAILURE;
        }
        if (dbEmailToken.getEmExpiresAt().isBefore(LocalDateTime.now())) {
            return ValidateEmailTokenResult.FAILURE_EXPIRED;
        }
        dbEmailToken.setEmUsed(true);
        if (this.emailTokenMapper.updateEmailToken(dbEmailToken) == 0) {
            throw new TransactionalException();
        }
        UserEntity user = this.userMapper.selectUserByEmail(emailToken.getEmEmail());
        user.setUsIsVerified(true);
        if (this.userMapper.updateUser(user) == 0) {
            throw new TransactionalException();
        }


        return CommonResult.SUCCESS;
    }
// endregion

    // region 아이디 , 닉네임 중복검사
    @Transactional
    public Result checkDuplicateUser(String userId) {
        if (this.userMapper.selectUserById(userId) != null) {
            return RegisterResult.FAILURE_DUPLICATE_ID;
        }
        if (userId == null || userId.length() < 6 || userId.length() > 12) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }

    @Transactional
    public Result checkDuplicateNickname(String nickname) {

        if (this.userMapper.selectUserByNickname(nickname) != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }
        if (nickname == null || nickname.length() < 2 || nickname.length() > 12) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;

    }
// endregion

    // region 아이디 찾기
    public Result findUserId(UserEntity user) {
        UserEntity dbUser = this.userMapper.FindUserByEmail(user.getUsName(), user.getUsEmail(), user.getUsContact());

        if (dbUser == null) {


            return CommonResult.FAILURE;
        }

        if (dbUser.isUsIsDeleted()) {
            return FindResult.FAILURE_DELETED;
        }
        if (dbUser.isUsIsSuspended()) {
            return LoginResult.FAILURE_SUSPENDED;
        }
        if (!dbUser.isUsIsVerified()) {
            return LoginResult.FAILURE_NOT_VERIFIED;
        }
        if (!user.getUsName().equals(dbUser.getUsName()) ||
                !user.getUsEmail().equals(dbUser.getUsEmail()) ||
                !user.getUsContact().equals(dbUser.getUsContact())) {
            return CommonResult.FAILURE;
        }
        user.setUsId(dbUser.getUsId());
        user.setUsName(dbUser.getUsName());
        user.setUsEmail(dbUser.getUsEmail());
        user.setUsContact(dbUser.getUsContact());

        return CommonResult.SUCCESS;
    }
    // endregion

    // region 비밀번호 찾기, 재설정
    // TODO 추후 비밀번호 재설정시 강도, REGEX 추가

    public Result findUserPassword(UserEntity user) {
        UserEntity dbUser = this.userMapper.FindUserById(user.getUsId(), user.getUsEmail(), user.getUsContact());
        if (dbUser == null) {

            return CommonResult.FAILURE;
        }
        if (dbUser.isUsIsDeleted()) {
            return FindResult.FAILURE_DELETED;
        }
        if (dbUser.isUsIsSuspended()) {
            return LoginResult.FAILURE_SUSPENDED;
        }
        if (!dbUser.isUsIsVerified()) {
            return LoginResult.FAILURE_NOT_VERIFIED;
        }
        if (!user.getUsId().equals(dbUser.getUsId()) || !user.getUsEmail().equals(dbUser.getUsEmail()) || !user.getUsContact().equals(dbUser.getUsContact())) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }


    @Transactional
    public Result provokeRecoverPassword(HttpServletRequest request, String email) throws MessagingException {
        if (email == null || email.length() < 2 || email.length() > 20) {

            return CommonResult.FAILURE;
        }
        UserEntity user = this.userMapper.selectUserByEmail(email);
        if (user == null || user.isUsIsDeleted()) {

        }
        EmailTokenEntity emailToken = new EmailTokenEntity();
        emailToken.setEmEmail(user.getUsEmail());
        emailToken.setEmKey(CryptoUtils.hashSha512(String.format("%s%s%f%f",
                user.getUsEmail(),
                user.getUsPw(),
                Math.random(),
                Math.random())));
        emailToken.setEmCreatedAt(LocalDateTime.now());
        emailToken.setEmExpiresAt(LocalDateTime.now().plusDays(24));
        emailToken.setEmUsed(false);

        if (this.emailTokenMapper.insertEmailToken(emailToken) == 0) {
            throw new TransactionalException();
        }
        String validationLink = String.format
                ("%s://%s:%d/user/find-password-result?userEmail=%s&key=%s&userId=%s",
                        request.getScheme(),
                        request.getServerName(),
                        request.getServerPort(),
                        emailToken.getEmEmail(),
                        emailToken.getEmKey(),
                        user.getUsId());

        Context context = new Context();
        context.setVariable("validationLink", validationLink);
        String mailText = this.templateEngine.process("email/recoverPassword", context);

        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("nyeonjae94@gmail.com");
        mimeMessageHelper.setTo(emailToken.getEmEmail());
        mimeMessageHelper.setSubject("[JGV] 비밀번호 재설정 인증 링크");
        mimeMessageHelper.setText(mailText, true);

        user.setUsEmail(user.getUsEmail());
        user.setUsId(user.getUsId());
        this.mailSender.send(mimeMessage);
        return CommonResult.SUCCESS;
    }

    public Result recoverEmail(UserEntity user) {
        if (user.getUsContact() == null || user.getUsContact().length() < 10 || user.getUsContact().length() > 12) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectUserByContact(user.getUsContact());
        if (dbUser == null || dbUser.isUsIsDeleted()) {
            return CommonResult.FAILURE;
        }
        user.setUsEmail(dbUser.getUsEmail());
        return CommonResult.SUCCESS;
    }


    @Transactional
    public Result resolveRecoverPassword(EmailTokenEntity emailToken, String password) {
        if (emailToken == null ||
                emailToken.getEmEmail() == null || emailToken.getEmEmail().length() < 8 || emailToken.getEmEmail().length() > 50 ||
                emailToken.getEmKey() == null || emailToken.getEmKey().length() != 128 ||
                password == null || password.length() < 8 || password.length() > 50) {
            return CommonResult.FAILURE;
        }
        EmailTokenEntity dbEmailToken =
                this.emailTokenMapper.selectEmailTokenByUserEmailAndKey(emailToken.getEmEmail(), emailToken.getEmKey());
        if (dbEmailToken == null || dbEmailToken.isEmUsed()) {
            return CommonResult.FAILURE;
        }
        if (dbEmailToken.getEmExpiresAt().isBefore(LocalDateTime.now())) {
            return ResolveRecoverPasswordResult.FAILURE_EXPIRED;
        }
        dbEmailToken.setEmUsed(true);
        if (this.emailTokenMapper.updateEmailToken(dbEmailToken) == 0) {
            throw new TransactionalException();
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserEntity user = this.userMapper.selectUserByEmail(emailToken.getEmEmail());
        user.setUsPw(passwordEncoder.encode(password));
        if (this.userMapper.updateUser(user) == 0) {
            throw new TransactionalException();
        }
        return CommonResult.SUCCESS;
    }
    // endregion

    // region 회원 정보 수정
    public Result modifyNickname(UserEntity user, String nickname) {

        if (user == null || user.isUsIsDeleted() || nickname.length() < 2 || nickname.length() > 10) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectUserByNickname(nickname);
        if (dbUser != null && !nickname.equals(user.getUsNickName())) {

            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }
        user.setUsNickName(nickname);
        if (this.userMapper.updateUser(user) == 0) {
            throw new TransactionalException();
        }

        return CommonResult.SUCCESS;
    }

    public Result modifyPassword(UserEntity user, String password) {
        String passwordRegex = "(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,100}$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        Matcher passwordMatcher = passwordPattern.matcher(password);
        if (!passwordMatcher.matches()) {
            return RegisterResult.FAILURE_INVALID_PASSWORD;
        }
        if (user == null || user.isUsIsDeleted() || password.length() < 8 || password.length() > 100) {
            return CommonResult.FAILURE;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.setUsPw(encoder.encode(password));

        if (this.userMapper.updateUser(user) == 0) {
            throw new TransactionalException();
        }
        return CommonResult.SUCCESS;
    }

    public Result reservationCancel(UserEntity user, PaymentEntity payment) {
        if (user == null || payment.getPaDeletedAt() == null || payment.getUsNum() < 0) {
            return CommonResult.FAILURE;
        }
        if (!payment.isPaState()) {

            return ReservationResult.FAILURE_CANCEL_COMPLETE;
        }
        // pa.state 가 이미 0일때 return ReservationResult.FAILURE_CANCEL_COMPLETE;

        payment.setPaState(false);
        return CommonResult.SUCCESS;
    }

    // endregion
}
