package dev.jwkim.jgv.controlles.errors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/error")
public class ErrorController {
    @RequestMapping(value = "/403")
    public ModelAndView getError403() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/403");
        return modelAndView;
    }

}
