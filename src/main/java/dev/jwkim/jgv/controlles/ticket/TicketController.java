package dev.jwkim.jgv.controlles.ticket;

import dev.jwkim.jgv.entities.theater.CinemaTypeEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.services.ticket.TicketService;
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
@RequestMapping(value = "/ticket")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(
    ) {
        ModelAndView modelAndView = new ModelAndView();
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

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
}