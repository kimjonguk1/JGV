package dev.jwkim.jgv.controlles.movie;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.services.movie.MovieService;
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
        boolean movie = this.movieService.insertAllMovies(movieEntity);
        mav.addObject("movie", movie);
        return mav;
    }
}
