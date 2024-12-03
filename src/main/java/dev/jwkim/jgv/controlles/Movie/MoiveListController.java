package dev.jwkim.jgv.controlles.Movie;

import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.services.Movie.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping(value = "/movies")
public class MoiveListController {
    private final MovieService movieService;

    public MoiveListController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping(value = "/movieList", method = RequestMethod.GET)
    public ModelAndView getMovieList() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("article/MovieList");
        List<Movie_ImageDTO> movies = this.movieService.selectAllMovieList();
        mav.addObject("movies", movies);
        return mav;
    }

    @RequestMapping(value = "/")
    public String getMovieDetail(@RequestParam(value = "id", required = true) Integer id) {
        return null;
    }
}
