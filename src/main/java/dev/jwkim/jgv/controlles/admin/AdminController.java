package dev.jwkim.jgv.controlles.admin;

import dev.jwkim.jgv.DTO.AdminMovieDTO;
import dev.jwkim.jgv.DTO.AdminTheaterDTO;
import dev.jwkim.jgv.DTO.AllMovieInfoDTO;
import dev.jwkim.jgv.DTO.MovieDeleteModifyDTO;
import dev.jwkim.jgv.DTO.ScreenInfoDTO;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.results.user.MovieDeleteModifyResult;
import dev.jwkim.jgv.results.user.ReviewResult;
import dev.jwkim.jgv.services.admin.AdminService;
import dev.jwkim.jgv.services.movie.MovieService;
import dev.jwkim.jgv.vos.PageVo;
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
    public ModelAndView getIsAdmin(Model model,
                                   @RequestParam(value = "page", required = false, defaultValue = "1") int page, @RequestParam(value = "filter", required = false) String filter,
                                   @RequestParam(value = "keyword", required = false) String keyword) {
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
        Pair<PageVo, Map<String, Map<String, List<ScreenInfoDTO>>>> groupedScreens =
                this.adminService.getGroupedScreens(page);
        modelAndView.addObject("groupedScreens", groupedScreens.getRight());  // groupedScreens의 데이터를 가져오기

        // 페이지네이션 정보
        modelAndView.addObject("screenPageVo", groupedScreens.getLeft());

        modelAndView.setViewName("admin/is_admin");

        return modelAndView;
    }

    //region  press x express joy

    @RequestMapping(value = "/movie-crawl", method = RequestMethod.GET, produces =
            MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMovieCrawl(@SessionAttribute UserEntity user,
                                      HttpServletResponse response) {

        if (!user.isUsIsAdmin()) {
            response.setStatus(404);

        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/movie-crawl");
        return modelAndView;
    }

    @RequestMapping(value = "/theater-crawl", method = RequestMethod.GET, produces =
            MediaType.TEXT_HTML_VALUE)
    public ModelAndView getTheaterCrawl(@SessionAttribute UserEntity user,
                                        HttpServletResponse response) {

        if (!user.isUsIsAdmin()) {
            response.setStatus(404);

        }
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

    //영화 수정 페이지
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
}
