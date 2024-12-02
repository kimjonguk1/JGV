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
        LocalDate today = LocalDate.now();
        List<Movie_ImageDTO> nowPlaying = movies.stream()
                .filter(movie -> !movie.getMoDate().isAfter(today)) // 개봉일이 오늘 또는 이전인 경우
                .toList();

        List<Movie_ImageDTO> upcomingMovies = movies.stream()
                .filter(movie -> movie.getMoDate().isAfter(today)) // 개봉일이 오늘 이후인 경우
                .toList();

        mav.addObject("nowPlaying", nowPlaying);
        mav.addObject("upcomingMovies", upcomingMovies);
//        mav.addObject("movies", movies);
        return mav;
    }

    @RequestMapping(value = "/")
    public String getMovieDetail(@RequestParam(value = "id", required = true) Integer id) {
        return null;
    }
}
