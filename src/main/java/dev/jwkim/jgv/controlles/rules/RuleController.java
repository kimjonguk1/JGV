package dev.jwkim.jgv.controlles.rules;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/rules")
public class RuleController {

    @RequestMapping(value = "/privacy", method = RequestMethod.GET)
    public ModelAndView getPrivacy() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("rules/privacy");
        return modelAndView;
    }

    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public ModelAndView getService() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("rules/service");
        return modelAndView;
    }
}
