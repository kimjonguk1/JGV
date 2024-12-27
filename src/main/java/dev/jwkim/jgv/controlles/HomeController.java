package dev.jwkim.jgv.controlles;

import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.services.movie.MovieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import java.util.List;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    public final MovieService movieService;

    public HomeController(MovieService movieService) {
        this.movieService = movieService;
    }

    //region 메인화면
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home/index");
        List<Movie_ImageDTO> currentMovies = this.movieService.selectCaraouselCurrnetMovieList();
        List<Movie_ImageDTO> upComingMovies = this.movieService.selectCarouselUpcomingMovieList();
        modelAndView.addObject("currentMovies", currentMovies);
        modelAndView.addObject("upComingMovies", upComingMovies);
        return modelAndView;
    }
    // endregion

    // region 로그아웃

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpSession session) {
        session.invalidate();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    // endregion
}
