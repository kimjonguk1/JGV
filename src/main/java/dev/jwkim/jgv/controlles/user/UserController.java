package dev.jwkim.jgv.controlles.user;


import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import dev.jwkim.jgv.entities.user.EmailTokenEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.exceptions.MessageRemovedException;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.services.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // region 회원가입
    @RequestMapping(value = "/register", method = RequestMethod.GET, produces =
            MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRegister() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/register");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegister(HttpServletRequest request, UserEntity user

    ) throws MessagingException {

        Result result = this.userService.register(request, user);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();

    }
// endregion

    // region 이메일 토큰
    @RequestMapping(value = "/validate-email-token", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getValidateEmailToken(EmailTokenEntity emailToken) {
        Result result = this.userService.validateEmailToken(emailToken);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(Result.NAME, result.nameToLower());
        modelAndView.setViewName("user/validateEmailToken");

        return modelAndView;
    }
// endregion

    //     region 로그인
    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getLogin(HttpSession session, @RequestParam(value = "redirect", required = false) String redirect) {
        // 이미 로그인된 경우
        if (session.getAttribute("user") != null) {
            return new ModelAndView("redirect:/");
        }
        if (redirect != null) {
            session.setAttribute("redirect", redirect);  // redirect 파라미터를 로그인 페이지로 전달
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/login");
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postLogin(UserEntity user, HttpSession session, @RequestParam(value = "redirect", required = false) String redirect) {
        Result result = this.userService.login(user);
        JSONObject response = new JSONObject();

        // 로그인 성공 시
        if (result == CommonResult.SUCCESS) {
            session.setAttribute("user", user);
            response.put("userNickname", user.getUsNickName());


            if (redirect == null) {
                redirect = (String) session.getAttribute("redirect");
                if (redirect == null) {
                    redirect = "/";  // 기본값 설정
                }
            }
            response.put("redirect", redirect);
            System.out.println(redirect);
        }

        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }



// endregion

    // http://localhost:8080/user/myPage/receipt

    // region 마이페이지


    @RequestMapping(value = "/myPage/{fragment}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPage(HttpServletResponse response, UserEntity user, HttpSession session, @PathVariable(value = "fragment") String fragment, HttpServletRequest request) throws ServletException, IOException {
        String[] validFragments = {"main", "reservation", "receipt", "personal", "withdraw"};
        if (fragment == null || Arrays.stream(validFragments).noneMatch(x -> x.equals(fragment))) {
            response.setStatus(404);
            return null;
        }

        if (session.getAttribute("user") == null) {
            String requestedUrl = request.getRequestURI(); // 현재 요청 URL을 가져옴
            // 포워드 방식으로 로그인 페이지로 이동
            request.setAttribute("redirect", requestedUrl);
            request.getRequestDispatcher("/user/login").forward(request, response); // 내부 요청 포워드
            return null;  // 포워드 후 반환할 필요는 없으므로 null
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("session", session);
        modelAndView.addObject("fragment", fragment);
        modelAndView.setViewName("user/myPage/myPage");

        return modelAndView;


    }


    @RequestMapping(value = "/myPage/personal", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView postMyPagePersonal(@SessionAttribute("user") UserEntity user,
                                           @RequestParam(value = "password") String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Boolean passwordMatches = encoder.matches(password, user.getUsPw()); // true, false, null

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("passwordMatches", passwordMatches);
        modelAndView.setViewName("user/myPage/modify");
        return modelAndView;
    }

//    @RequestMapping(value = "/myPage/modify", method = RequestMethod.GET, produces =
//            MediaType.TEXT_HTML_VALUE)
//    public ModelAndView getModify(HttpSession session, UserEntity user) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("session", session);
//        modelAndView.addObject("user", user);
//        modelAndView.setViewName("user/myPage/modify");
//        return modelAndView;
//    }
    // endregion

    // region 아이디 / 닉네임 중복 검사
    @RequestMapping(value = "/check-duplicate-id", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String checkDuplicateId(@RequestParam("user") String user) {
        Result result = userService.checkDuplicateUser(user);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/check-duplicate-nickname", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String checkDuplicateNickname(@RequestParam("nickname") String nickname) {
        Result result = userService.checkDuplicateNickname(nickname);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
// endregion

    // region 아이디 / 비밀번호 찾기
    @RequestMapping(value = "find-id", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getFindId(UserEntity user) {
        Result result = userService.findUserId(user);
        ModelAndView modelAndView = new ModelAndView();
        if (result == CommonResult.SUCCESS) {
            modelAndView.addObject("name", user.getUsName());
            modelAndView.addObject("id", user.getUsId());
        }
        modelAndView.setViewName("user/find-id");
        return modelAndView;
    }

    @RequestMapping(value = "find-id", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postFindId(UserEntity user) {

        Result result = userService.findUserId(user);
        JSONObject response = new JSONObject();
        if (result == CommonResult.SUCCESS) {
            response.put("name", user.getUsName());
            response.put("id", user.getUsId());
        }
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }


    @RequestMapping(value = "find-password", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getFindPassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/find-password");
        return modelAndView;
    }

    @RequestMapping(value = "find-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postFindPassword(UserEntity user, HttpServletRequest request, @RequestParam(value = "usEmail", required = false) String usEmail) throws MessagingException {
        Result result = userService.findUserPassword(user);
        this.userService.provokeRecoverPassword(request, usEmail);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());

        return response.toString();
    }


    @RequestMapping(value = "/recover-email", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String recoverEmail(UserEntity user) {
        Result result = this.userService.recoverEmail(user);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        if (result == CommonResult.SUCCESS) {
            response.put("email", user.getUsEmail());
        }
        return response.toString();
    }

    @RequestMapping(value = "/find-password-result", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ModelAndView getRecoverPassword(@RequestParam(value = "userEmail", required =
                                                   false) String userEmail,
                                           @RequestParam(value = "key", required = false) String key,
                                           @RequestParam(value = "userId", required =
                                                   false) String userId
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userEmail", userEmail);
        modelAndView.addObject("key", key);
        modelAndView.addObject("userId", userId);

        modelAndView.setViewName("user/find-password-result");
        return modelAndView;
    }
    // endregion

    // region 비밀번호 수정
    @RequestMapping(value = "/find-password-result", method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchRecoverPassword(
            @RequestParam(value = "usPw", required = false) String password,
            @RequestParam(value = "userEmail", required = false) String email,
            @RequestParam(value = "key", required = false) String key) {

        EmailTokenEntity emailToken = new EmailTokenEntity();
        emailToken.setEmEmail(email);
        emailToken.setEmKey(key);

        Result result = this.userService.resolveRecoverPassword(emailToken, password);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());

        return response.toString();
    }
    // endregion

    // region 회원 수정 팝업창
    @RequestMapping(value = "/myPage/modifyNickname", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView patchModifyNickname(HttpSession session, UserEntity user) {


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("session", session);
        modelAndView.addObject("user", user);
        UserEntity users = (UserEntity) session.getAttribute("user");


        modelAndView.setViewName("user/myPage/modify-nickname");
        return modelAndView;
    }

    @RequestMapping(value = "/myPage/modifyNickname", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchModifyNickname(HttpSession session, UserEntity user,
                                      @RequestParam(value = "usNickname", required = false) String nickname) {
        UserEntity users = (UserEntity) session.getAttribute("user");
        Result result = this.userService.modifyNickname(users, nickname);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());

        return response.toString();
    }

    @RequestMapping(value = "/myPage/modifyPassword", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModifyPassword(HttpSession session, UserEntity user) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("session", session);
        modelAndView.addObject("user", user);
        modelAndView.setViewName("user/myPage/modify-password");
        return modelAndView;
    }

    @RequestMapping(value = "/myPage/modifyPassword", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchModifyPassword(HttpSession session, UserEntity user,
                                      @RequestParam(value = "usPw", required = false) String password) {
        UserEntity users = (UserEntity) session.getAttribute("user");
        Result result = this.userService.modifyPassword(users, password);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());

        return response.toString();
    }

    @RequestMapping(value = "/myPage/reservationCancel", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getReservationCancel(HttpSession session, UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("session", session);
        modelAndView.addObject("user", user);
        modelAndView.setViewName("user/myPage/reservation-cancel");
        return modelAndView;
    }

    @RequestMapping(value = "/myPage/reservationCancel", method = RequestMethod.PATCH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchReservationCancel(HttpSession session, UserEntity user) {
        UserEntity users = (UserEntity) session.getAttribute("user");
        Result result = this.userService.reservationCancel(users);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());

        return response.toString();
    }

    // endregion

    // region 회원 탈퇴

    @RequestMapping(value = "/myPage/userWithdraw", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUserWithdraw(HttpSession session, UserEntity user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("session", session);
        modelAndView.addObject("user", user);
        modelAndView.setViewName("user/myPage/userWithdraw");
        return modelAndView;
    }

    @RequestMapping(value = "/myPage/userWithdraw", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteUser(HttpSession session, UserEntity user) {
        UserEntity users = (UserEntity) session.getAttribute("user");
        Result result = this.userService.withdrawUser(users);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
    // endregion


}
