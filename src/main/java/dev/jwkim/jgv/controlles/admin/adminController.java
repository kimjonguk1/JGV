package dev.jwkim.jgv.controlles.admin;

import dev.jwkim.jgv.entities.user.UserEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/admin")
public class adminController {


    @RequestMapping(value = "/is_admin", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIsAdmin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/admin/is_admin");
        return modelAndView;
    }

    //region  press x express joy

    @RequestMapping(value = "/movie-crawl", method = RequestMethod.GET, produces =
            MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMovieCrawl(@SessionAttribute dev.jwkim.jgv.entities.user.UserEntity user,
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
}
