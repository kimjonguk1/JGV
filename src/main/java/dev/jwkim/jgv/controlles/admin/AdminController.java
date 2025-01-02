package dev.jwkim.jgv.controlles.admin;

import dev.jwkim.jgv.DTO.AdminMovieDTO;
import dev.jwkim.jgv.DTO.AdminTheaterDTO;
import dev.jwkim.jgv.DTO.AllMovieInfoDTO;
import dev.jwkim.jgv.DTO.MovieDeleteModifyDTO;
import dev.jwkim.jgv.DTO.ScreenInfoDTO;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.results.user.MovieDeleteModifyResult;
import dev.jwkim.jgv.results.user.ReviewResult;
import dev.jwkim.jgv.services.admin.AdminService;
import dev.jwkim.jgv.services.movie.MovieService;
import dev.jwkim.jgv.vos.PageVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private final AdminService adminService;
    private final MovieService movieService;

    public AdminController(AdminService adminService, MovieService movieService) {
        this.adminService = adminService;
        this.movieService = movieService;
    }

    @RequestMapping(value = "/is_admin", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIsAdmin(Model model, @SessionAttribute(value = "user") UserEntity user,
                                   HttpServletRequest request,HttpServletResponse response,
                                   @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                   @RequestParam(value = "filter", required = false) String filter,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "screen-filter", required = false) String screenFilter,
                                   @RequestParam(value = "screen-keyword", required = false) String screenKeyword)

                                    throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        // 영화 페이징
        if (filter == null && keyword == null) {
            Pair<PageVo, AdminMovieDTO[]> pair = this.adminService.selectMoviePage(page);
            modelAndView.addObject("pageVo", pair.getLeft());
            modelAndView.addObject("adminDTO", pair.getRight());
        } else {
            Pair<PageVo, AdminMovieDTO[]> pair = this.adminService.searchMoviePage(page, filter, keyword);
            modelAndView.addObject("pageVo", pair.getLeft());
            modelAndView.addObject("adminDTO", pair.getRight());
            modelAndView.addObject("filter", filter);
            modelAndView.addObject("keyword", keyword);
        }

        // 상영정보 그룹화
        if (screenFilter == null && screenKeyword == null) {
            Pair<PageVo, Map<String, Map<String, List<ScreenInfoDTO>>>> groupedScreens =
                    this.adminService.getGroupedScreens(page);
            modelAndView.addObject("groupedScreens", groupedScreens.getRight());  // groupedScreens의 데이터를 가져오기

            // 페이지네이션 정보
            modelAndView.addObject("screenPageVo", groupedScreens.getLeft());
        } else {
            Pair<PageVo, Map<String, Map<String, List<ScreenInfoDTO>>>> groupedScreens =
                    this.adminService.searchGroupedScreens(page, screenFilter, screenKeyword);
            modelAndView.addObject("groupedScreens", groupedScreens.getRight());  // groupedScreens의 데이터를 가져오기

            // 페이지네이션 정보
            modelAndView.addObject("screenPageVo", groupedScreens.getLeft());
            modelAndView.addObject("screenFilter", screenFilter);
            modelAndView.addObject("screenKeyword", screenKeyword);
        }

        if (user == null || !user.isUsIsAdmin()) {
           modelAndView.setViewName("redirect:/error");
           return modelAndView;
        }
        modelAndView.setViewName("admin/is_admin");
        return modelAndView;
    }

    //region  press x express joy

    @RequestMapping(value = "/movie-crawl", method = RequestMethod.GET, produces =
            MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMovieCrawl() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/movie-crawl");
        return modelAndView;
    }

    @RequestMapping(value = "/theater-crawl", method = RequestMethod.GET, produces =
            MediaType.TEXT_HTML_VALUE)
    public ModelAndView getTheaterCrawl() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/theater-crawl");
        return modelAndView;
    }
    // endregion

    //영화 삭제(종영일 설정)
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MovieDeleteModifyResult deleteMovie(HttpSession session, @RequestBody MovieDeleteModifyDTO request) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
        if (loggedInUser == null) {
            return MovieDeleteModifyResult.NOT_LOGGED_IN;
        }
        int movieId = request.getMoNum();
        boolean result = this.movieService.updateMoEndingToNow(movieId);

        return result ? MovieDeleteModifyResult.SUCCESS : MovieDeleteModifyResult.FAILURE;
    }

    //region 영화 수정 페이지
    @RequestMapping(value = "/modify/{movieNum}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModifyMoviePage(@PathVariable("movieNum") int movieNum, HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");

        if (loggedInUser == null) {
            return new ModelAndView("redirect:/user/login?forward=/user/myPage/main\n");
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/movie-modify");
        return modelAndView;
    }

    @RequestMapping(value = "/api/{movieNum}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AllMovieInfoDTO> getMovieInfo(@PathVariable("movieNum") int movieNum) {
        AllMovieInfoDTO movie = movieService.getMoviesById(movieNum);
        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(movie);
    }

    // 클라이언트가 보내는 데이터의 형식은 consumes, 서버가 응답을로 반환할 데이터의 형식은 produces
    @RequestMapping(value = "/api/{movieNum}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MovieDeleteModifyResult modifyMovie(@PathVariable("movieNum") int movieNum, @RequestBody AllMovieInfoDTO movieDTO, HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");

        // 로그인 상태 확인
        if (loggedInUser == null) {
            return MovieDeleteModifyResult.NOT_LOGGED_IN;
        }
        System.out.println("Received movieInfo: " + movieDTO);
        // 영화 수정 로직
        boolean isUpdated = movieService.updateMovie(movieNum, movieDTO);
        if(isUpdated) {
            return MovieDeleteModifyResult.SUCCESS;
        } else {
            return MovieDeleteModifyResult.FAILURE;
        }
    }

    //endregion


  
    @RequestMapping(value = "/is_admin", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String modifyIsAdmin(@RequestParam(value = "scNum", required = false, defaultValue = "0") int scNum,
                                @RequestParam(value = "scStartDate", required = false) String scStartDate) {
        Result result = this.adminService.modifyScreen(scNum, scStartDate);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/is_admin", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteIsAdmin(@RequestParam(value = "scNum", required = false, defaultValue = "0") int scNum) {
        Result result = this.adminService.deleteScreen(scNum);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }
}
