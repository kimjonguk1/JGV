package dev.jwkim.jgv.controlles.user;


import dev.jwkim.jgv.DTO.MyReviewDTO;
import dev.jwkim.jgv.entities.user.EmailTokenEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.services.user.ReviewService;
import dev.jwkim.jgv.services.user.UserService;
import dev.jwkim.jgv.vos.PageVo;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    // region 회원가입
    @RequestMapping(value = "/register", method = RequestMethod.GET, produces =
            MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRegister(@SessionAttribute(value = "user", required = false) UserEntity user) {

        ModelAndView modelAndView = new ModelAndView();
        if (user != null) {
            modelAndView.setViewName("redirect:/error");
            return modelAndView;
        }
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
    public ModelAndView getLogin(HttpSession session) {
        // 이미 로그인된 경우
        if (session.getAttribute("user") != null) {
            return new ModelAndView("redirect:/error");
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/login");

        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postLogin(UserEntity user, HttpServletRequest request) {
        // 로그인 시도 시 IP 정보
        String currentIp = request.getRemoteAddr();

        // 로그인 시도 결과를 담을 변수
        Result result = this.userService.login(user);
        JSONObject response = new JSONObject();

        // 로그인 성공 시
        if (result == CommonResult.SUCCESS) {
            System.out.println("로그인 직후 세션 : " + currentIp );
            // 기존 세션의 사용자 정보와 IP 체크
            HttpSession session = request.getSession(false);  // 기존 세션을 가져옴 (새로운 세션을 만들지 않음)
            if (session != null && session.getAttribute("user") != null) {
                UserEntity sessionUser = (UserEntity) session.getAttribute("user");
                String storedIp = (String) session.getAttribute("ip");

                // 만약 기존 세션의 IP와 현재 요청의 IP가 다르면
                if (storedIp != null && !storedIp.equals(currentIp)) {
                    // 기존 세션을 무효화
                    session.invalidate();
                    System.out.println("강제로그아웃 세션 : " + session.getAttribute("user"));

                    // 강제 로그아웃 메시지 추가
                    response.put("message", "다른 IP에서 로그인 시도가 감지되었습니다. 로그아웃되었습니다.");
                    response.put("logout", true);
                }
            }

            // 새 세션 생성 및 사용자 정보 저장
            HttpSession newSession = request.getSession(true);  // 새 세션을 생성
            newSession.setAttribute("user", user);
            newSession.setAttribute("ip", currentIp);  // 현재 IP를 세션에 저장

            // 로그인 성공 시 사용자 번호 반환
            response.put("MemberNum", user.getUsNum());
        }

        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
// endregion

    // http://localhost:8080/user/myPage/receipt

    // region 마이페이지


    @RequestMapping(value = "/myPage/{fragment}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyPage(HttpServletResponse response, UserEntity user,HttpSession session,@PathVariable(value = "fragment") String fragment, HttpServletRequest request, @RequestParam(value = "page", required = false, defaultValue = "1")int page, @RequestParam(value = "page2", required = false, defaultValue = "1")int page2) throws ServletException, IOException {
        String[] validFragments = {"main", "reservation", "receipt", "personal", "withdraw"};
        if (fragment == null || Arrays.stream(validFragments).noneMatch(x -> x.equals(fragment))) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("redirect:/error");
            return modelAndView;
        }

        if (session == null) {
            // 현재 URI를 가져오고 쿼리 파라미터를 붙여서 forward URL로 전달
            String currentUrl = request.getRequestURI();  // 현재 URI만 추출
            String queryString = request.getQueryString();  // 쿼리 스트링 (있는 경우만)

            // 쿼리 스트링이 있으면 포함시켜서 forward 파라미터로 전달
            String redirectUrl = request.getContextPath() + "/user/login?forward=" + currentUrl;
            if (queryString != null) {
                redirectUrl += "?" + queryString;
            }

            response.sendRedirect(redirectUrl);  // 로그인 페이지로 리디렉션
            return null;
        }


        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");

        // 현재 날짜를 model에 추가
        LocalDateTime currentDate = LocalDateTime.now();
        String formattedCurrentDate = currentDate.toString();
        // 예약 정보 가져오기
        Pair<PageVo, Map<Set<String>, List<String>>> reservations = this.userService.reservationInformation(loggedInUser.getUsNum(), page2); // 예약 정보
        List<List<String>> cancelReservations = this.userService.selectCancelPaymentByUsNum(loggedInUser.getUsNum()); // 취소 정보

        int allReservations = this.userService.findAllReservations(loggedInUser.getUsNum());

        Pair<PageVo, List<MyReviewDTO>> pair = reviewService.getReviewByUser(page, loggedInUser.getUsNum());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageVo", pair.getLeft());
        modelAndView.addObject("Reviews", pair.getRight());
        modelAndView.addObject("user", user);
        modelAndView.addObject("session", session);
        modelAndView.addObject("fragment", fragment);
        modelAndView.addObject("currentDate", formattedCurrentDate); // 현재 날짜 전달
        modelAndView.addObject("PageReservations", reservations.getLeft());
        modelAndView.addObject("reservations", reservations.getRight());
        modelAndView.addObject("allReservations", allReservations);
        modelAndView.addObject("cancelReservations", cancelReservations);
        modelAndView.addObject("page", page); // 현재 page
        modelAndView.addObject("page2", page2); // 현재 page2
        modelAndView.setViewName("user/myPage/myPage");
        
        return modelAndView;

    }


    @RequestMapping(value = "/myPage/personal", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView postMyPagePersonal(@SessionAttribute("user") UserEntity user,
                                           @RequestParam(value = "password") String password,
                                           @RequestParam(value = "forward", required = false) String forward) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Boolean passwordMatches = encoder.matches(password, user.getUsPw()); // true, false, null

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("passwordMatches", passwordMatches);

        if (passwordMatches != null && passwordMatches) {
            // 인증 성공 시 forward 경로로 이동
            if (forward != null && !forward.isEmpty()) {
                modelAndView.setViewName("redirect:" + forward);
            } else {
                modelAndView.setViewName("user/myPage/modify"); // 기본 경로
            }

        }
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
    public String checkDuplicateId(@RequestParam("user") String userId, UserEntity user) {
        Result result = userService.checkDuplicateUser(userId, user);
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
    public ModelAndView getFindId(HttpSession session, UserEntity user) {
        Result result = userService.findUserId(user);
        ModelAndView modelAndView = new ModelAndView();

        if (session.getAttribute("user") != null) {
            return new ModelAndView("redirect:/error");
        }

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
    public ModelAndView getFindPassword(HttpSession session) {

        ModelAndView modelAndView = new ModelAndView();
        if (session.getAttribute("user") != null) {
            return new ModelAndView("redirect:/error");
        }
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
        if (userId == null || userEmail == null || key == null) {
            modelAndView.setViewName("redirect:/error");
            return modelAndView;
        }
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

    // 예매 취소

    @RequestMapping(value = "/myPage/reservationCancel", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getReservationCancel(@SessionAttribute(value = "user", required = false) UserEntity user) {

        ModelAndView modelAndView = new ModelAndView();
        if (user == null) {
            modelAndView.setViewName("redirect:/error");
            return modelAndView;
        }
        modelAndView.addObject("user", user);
        modelAndView.setViewName("user/myPage/reservation-cancel");
        return modelAndView;
    }

    @RequestMapping(value = "/myPage/reservationCancel", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchReservationCancel(HttpSession session,
                                         @RequestParam(value = "usNum", required = false) int usNum,
                                         @RequestParam(value = "paNum", required = false) int paNum,
                                         @RequestParam(value = "paPrice", required = false) int paPrice,
                                         @RequestParam(value = "paCreatedAt", required = false) String paCreatedAt) {

//        UserEntity users = (UserEntity) session.getAttribute("user");

        // 예매 취소 서비스 호출
        Result result = this.userService.reservationCancel(usNum, paNum, paPrice, paCreatedAt);
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
