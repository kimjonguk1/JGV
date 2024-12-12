package dev.jwkim.jgv.services.user;

import dev.jwkim.jgv.entities.user.EmailTokenEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.exceptions.TransactionalException;
import dev.jwkim.jgv.mappers.user.EmailTokenMapper;
import dev.jwkim.jgv.mappers.user.UserMapper;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.results.user.LoginResult;
import dev.jwkim.jgv.results.user.RegisterResult;
import dev.jwkim.jgv.results.user.ValidateEmailTokenResult;
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
            System.out.println(user.getUsId());
            System.out.println(user.getUsPw());
            System.out.println(user.getUsName());
            System.out.println(user.getUsNickName());
            System.out.println(user.getUsBirth());
            System.out.println(user.getUsGender());
            System.out.println(user.getUsEmail());
            System.out.println(user.getUsContact());
            System.out.println(user.getUsAddr());

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
// endregion

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
        return CommonResult.SUCCESS;
    }

    @Transactional
    public Result checkDuplicateNickname(String nickname) {

        if (this.userMapper.selectUserByNickname(nickname) != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }
        return CommonResult.SUCCESS;
    }
// endregion
}
