package dev.jwkim.jgv.controlles.admin;

import dev.jwkim.jgv.DTO.AdminMovieDTO;
import dev.jwkim.jgv.DTO.AdminTheaterDTO;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.services.admin.AdminService;
import dev.jwkim.jgv.vos.PageVo;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @RequestMapping(value = "/is_admin", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIsAdmin(Model model,
                                   @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        ModelAndView modelAndView = new ModelAndView();

        // 영화 페이징
        Pair<PageVo, AdminMovieDTO[]> pair = this.adminService.selectMoviePage(page);
        modelAndView.addObject("pageVo", pair.getLeft());
        modelAndView.addObject("adminDTO", pair.getRight());

        // 상영정보 페이징
        Pair<PageVo, AdminTheaterDTO[]> thPair = this.adminService.selectTheaterPage(page);
        modelAndView.addObject("pageVos", thPair.getLeft());
        modelAndView.addObject("adminThDTO", thPair.getRight());

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
}
