package dev.jwkim.jgv.controlles.movie;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.services.movie.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class MovieController {
    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Scheduled(cron = "0 0 12 * * ?")
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
