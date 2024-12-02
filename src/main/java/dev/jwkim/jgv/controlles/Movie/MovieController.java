package dev.jwkim.jgv.controlles.Movie;

import dev.jwkim.jgv.entities.Movie.MovieEntity;
import dev.jwkim.jgv.services.Movie.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping(value = "/crawling", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView crawling(MovieEntity movieEntity) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("article/Crawling");
        this.movieService.insertAllMovies(movieEntity);
        mav.addObject("message", "크롤링이 완료되었습니다.");
        return mav;
    }
}
