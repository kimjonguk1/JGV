package dev.jwkim.jgv.controlles.theater;

import dev.jwkim.jgv.entities.theater.RegionEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.services.theater.TheaterService;
import dev.jwkim.jgv.vos.theater.TheaterVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
@RequestMapping(value = "/theater")
@RequiredArgsConstructor
public class TheaterController {
    private final TheaterService theaterService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "theater", required = false) String theater) {
        ModelAndView modelAndView = new ModelAndView();
        RegionEntity[] regions = this.theaterService.findRegionAll();
        TheaterEntity[] theaters = this.theaterService.getTheatersByRegion(region);
        if (theater != null) {
            TheaterVo[] theaterVos = this.theaterService.selectAllTheaters(theater);
            Map<Set<String>, List<Set<String>>> theaterMap = new HashMap<>();
            Set<String> keys = new LinkedHashSet<>();
            List<Set<String>> values = new ArrayList<>();
            Set<String> types = new LinkedHashSet<>();
            SortedSet<String> times = new TreeSet<>();
            for (TheaterVo theaterVo : theaterVos) {
                keys.add(theaterVo.getThName());
                keys.add(theaterVo.getThAddr().split("\n")[0]);
                keys.add(theaterVo.getThAddr().split("\n")[1]);
                keys.add(theaterVo.getThImg());
                keys.add(String.valueOf(theaterVo.getSeatCount()));
                keys.add(String.valueOf(theaterVo.getCinemaCount()));
                if (theaterVo.getCitName().equals("4DX")) {
                    theaterVo.setCitName("DX");
                }
                types.add(theaterVo.getCitName());
                times.add(String.valueOf(theaterVo.getScStartDate()).split("T")[0]);
            }
            values.add(types);
            values.add(times);
            theaterMap.computeIfAbsent(keys, k -> new ArrayList<>()).addAll(values);
            System.out.println(theaterMap);
            modelAndView.addObject("theaterVos", theaterMap);
        }
//        Map<String, List<String>> maps = this.theaterService.Crawl();
        modelAndView.addObject("regions", regions);
        modelAndView.addObject("theaters", theaters);
//        modelAndView.addObject("maps", maps);
        modelAndView.setViewName("theater/index");
        return modelAndView;
    }
}
