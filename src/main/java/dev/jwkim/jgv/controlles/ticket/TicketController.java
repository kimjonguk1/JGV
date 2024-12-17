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
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping(value = "/ticket")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final TheaterService theaterService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(@RequestParam(value = "region", required = false) String region,
                                 @RequestParam(value = "moTitle", required = false) String moTitle) {
        ModelAndView modelAndView = new ModelAndView();
        MovieVo[] movies = this.ticketService.selectAllMoviesByRating();
        MovieVo[] movieVos = this.ticketService.selectAllMovies(moTitle);
        RegionVo[] regions = this.ticketService.selectRegionAndTheaterCount();
        TheaterEntity[] theaters = this.theaterService.getTheatersByRegion(region);
        Map<String, String> maps = this.ticketService.getWeekdays();
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
            @RequestParam(value = "thName", required = false) String thName
    ) {
        JSONObject response = new JSONObject();
        ReservationEntity[] seatNum = this.ticketService.selectSeatByReservationNum(ciName, thName);
        SeatEntity[] seatName = this.ticketService.selectSeatBySeatName(ciName, thName);
        CinemaTypeEntity[] citPrice = this.ticketService.selectSeatByCitPrice(ciName, thName);
        response.put(Result.NAME, seatNum);
        response.put(Result.NAMES, seatName);
        response.put(Result.NAMESS, citPrice);
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
                            int usNum) {
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