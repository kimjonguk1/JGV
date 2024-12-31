package dev.jwkim.jgv.controllerAdvice;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;


@ControllerAdvice
public class GlobalControllerAdvice {

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getError() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorCode", "500");
        modelAndView.setViewName("error");
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) throws ModelAndViewDefiningException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("error", ex.getMessage());
        modelAndView.setViewName("error");
        throw new ModelAndViewDefiningException(modelAndView);
    }

}