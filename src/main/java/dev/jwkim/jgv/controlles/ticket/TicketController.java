package dev.jwkim.jgv.controlles.ticket;

import dev.jwkim.jgv.entities.theater.CinemaTypeEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.services.theater.TheaterService;
import dev.jwkim.jgv.services.ticket.TicketService;
import dev.jwkim.jgv.vos.theater.MovieVo;
import dev.jwkim.jgv.vos.theater.RegionVo;
import dev.jwkim.jgv.vos.theater.ScreenVo;
import dev.jwkim.jgv.vos.ticket.CinemaTypeVo;
import dev.jwkim.jgv.vos.ticket.SeatVo;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/ticket")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final TheaterService theaterService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(@RequestParam(value = "region", required = false) String region,
                                 @RequestParam(value = "moTitle", required = false) String moTitle,
                                 @RequestParam(value = "thName", required = false) String thName,
                                 @RequestParam(value = "scStartDate", required = false) String scStartDate) {
        ModelAndView modelAndView = new ModelAndView();
        MovieVo[] movies = this.ticketService.selectAllMoviesByRating();
        MovieVo[] movieVos = this.ticketService.selectAllMovies(moTitle);
        RegionVo[] regions = this.ticketService.selectRegionAndTheaterCount();
        TheaterEntity[] theaters = this.theaterService.getTheatersByRegion(region);
        Map<String, String> maps = this.ticketService.getWeekdays();
        if (moTitle != null && thName != null && scStartDate != null) {
            ScreenVo[] screens = this.ticketService.selectScreenDatesByMovieAndTheaterAndDate(moTitle, thName, scStartDate);
            List<List<String>> contentLists = new ArrayList<>();
            Map<List<String>, List<List<String>>> times = new HashMap<>();
            for (ScreenVo screen : screens) {
                List<String> keys = new ArrayList<>();
                List<String> contents = new ArrayList<>();
                keys.add(screen.getCitName());
                // 2D
                keys.add(screen.getCiName());
                // 5관
                keys.add(String.valueOf(screen.getSeatCount()));
                // 40석
                contents.add(String.valueOf(screen.getScStartDate()));
                contents.add(String.valueOf(screen.getSeatCount()));
                contentLists.add(contents);
                times.computeIfAbsent(keys, k -> new ArrayList<>()).add(contents);
            }
            System.out.println(times);
            modelAndView.addObject("times", times);
        }
        modelAndView.addObject("movies", movies);
        modelAndView.addObject("movieVos", movieVos);
        modelAndView.addObject("regions", regions);
        modelAndView.addObject("theaters", theaters);
        modelAndView.addObject("maps", maps);
        modelAndView.setViewName("ticket/index");
        return modelAndView;
    }

    @RequestMapping(value = "/movies", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMovies() {
        ModelAndView modelAndView = new ModelAndView();
        MovieVo[] movies = this.ticketService.selectAllMoviesByKorea();
        modelAndView.addObject("movies", movies);
        modelAndView.setViewName("ticket/index");
        return modelAndView;
    }

    @RequestMapping(value = "/crawling", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView Crawl(ScreenEntity screen) {
        ModelAndView modelAndView = new ModelAndView();
        this.ticketService.Crawl(screen);
        modelAndView.setViewName("ticket/crawling");
        return modelAndView;
    }

    // --------------------------------------

    @RequestMapping(value = "/seat", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getSeat(
            @RequestParam(value = "ciName", required = false) String ciName,
            @RequestParam(value = "thName", required = false) String thName,
            @RequestParam(value = "moTitle", required = false) String moTitle,
            @RequestParam(value = "scStartDate", required = false) LocalDateTime scStartDate
    ) {
        JSONObject response = new JSONObject();
        SeatVo[] seatNum = this.ticketService.selectSeatByReservationNum(ciName, thName, moTitle, scStartDate);
        CinemaTypeVo[] citPrice = this.ticketService.selectSeatByCitPrice(ciName, thName, moTitle, scStartDate);
        response.put(Result.NAME, seatNum);
        response.put(Result.NAMES, citPrice);
        return response.toString();
    }

    @RequestMapping(value = "/ReservationRefundRegulations", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getReservationRefundRegulations(
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("ticket/ReservationRefundRegulations");
        return modelAndView;
    }


    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postIndex(@RequestParam(value = "meName", required = false) String meName,
                            @RequestParam(value = "paPrice", required = false) int paPrice,
                            @RequestParam(value = "usNum", required = false)
                            int usNum,
                            @RequestParam(value = "seName", required = false)
                            int seName) {
        Result result = this.ticketService.insertPayment(meName, paPrice, usNum);
        JSONObject response = new JSONObject();
        response.put(Result.NAME, result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/reservation", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getReservation(
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("ticket/reservation");
        return modelAndView;
    }
}