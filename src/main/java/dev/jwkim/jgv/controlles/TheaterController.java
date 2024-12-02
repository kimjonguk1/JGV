package dev.jwkim.jgv.controlles;

import dev.jwkim.jgv.entities.RegionEntity;
import dev.jwkim.jgv.entities.TheaterEntity;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.services.TheaterService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/theater")
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(@RequestParam(value = "region", required = false) String region) {
        ModelAndView modelAndView = new ModelAndView();
        RegionEntity[] regions = this.theaterService.findRegionAll();
        TheaterEntity[] theater = this.theaterService.findTheaterAll();
        TheaterEntity[] theaters = this.theaterService.getTheatersByRegion(region);
        String[] a = new String[theater.length];
        for (TheaterEntity theatersss : theater) {
            a = theatersss.getThAddr().split("\n");
        }
        modelAndView.addObject("regions", regions);
        modelAndView.addObject("theater", theater);
        modelAndView.addObject("theaters", theaters);
        modelAndView.addObject("a", a);
        modelAndView.setViewName("theater/index");
        return modelAndView;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getRegionIndex(@RequestParam(value = "region", required = false) String region) {
        JSONObject response = new JSONObject();
        TheaterEntity[] theaters = this.theaterService.getTheatersByRegion(region);
        response.put(Result.NAME, theaters);
        return response.toString();
    }
}
