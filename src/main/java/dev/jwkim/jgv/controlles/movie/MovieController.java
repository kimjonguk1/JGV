package dev.jwkim.jgv.controlles.movie;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.services.movie.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@EnableScheduling
@RequestMapping("/admin")
public class MovieController {
    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }


    // 주기적으로 실행되는 크롤링 작업 (매개변수 없음)
    @Scheduled(cron = "0 0 0 * * ?") // 매일 0시 0분에 실행
    public void scheduledCrawling() {
        this.movieService.insertAllMovies(new MovieEntity());
        System.out.println("크롤링이 완료되었습니다."); // 콘솔 로그 출력
    }

    // 수동으로 크롤링을 트리거하는 HTTP 요청 핸들러
    @RequestMapping(value = "/crawling", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView manualCrawling() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("article/Crawling");
        this.movieService.insertAllMovies(new MovieEntity());
        mav.addObject("message", "크롤링이 완료되었습니다.");
        return mav;
    }
}
