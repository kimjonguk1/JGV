package dev.jwkim.jgv.controlles.ticket;

import dev.jwkim.jgv.entities.theater.RegionEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.services.theater.TheaterService;
import dev.jwkim.jgv.services.ticket.TicketService;
import dev.jwkim.jgv.vos.theater.*;
import dev.jwkim.jgv.vos.ticket.CinemaTypeVo;
import dev.jwkim.jgv.vos.ticket.SeatVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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
        RegionVo[] regions = this.ticketService.selectRegionAndTheaterCount();
        TheaterEntity[] theaters = this.theaterService.getTheatersByRegion(region);
        Map<String, String> maps = this.ticketService.getWeekdays();
        if (moTitle != null && thName == null && scStartDate == null && region == null) {
            MovieVo[] movieVos = this.ticketService.selectAllMovies(moTitle);
            List<Object[]> vos = new ArrayList<>();
            Map<String, String> moMaps = this.ticketService.getWeekdaysByMoTitle(moTitle);
            Set<String> keys = new LinkedHashSet<>();
            SortedSet<String> thKeys = new TreeSet<>();
            for (MovieVo vo : movieVos) {
                keys.add(vo.getMoTitle());
                keys.add(vo.getMImgUrl());
                keys.add(vo.getRaGrade());
                keys.add(String.valueOf(vo.getTheaterCount()));
                keys.add(vo.getRegName());
                keys.add(String.valueOf(vo.getMoNum()));
                thKeys.add(vo.getThName());
            }
            vos.add(new Object[]{keys, thKeys, moMaps});
            modelAndView.addObject("movieVos", vos);
        }
        if (moTitle != null && thName == null && scStartDate == null && region != null) {
            MovieVo[] movieVos = this.ticketService.selectAllMoviesByRegion(moTitle, region);
            List<Object[]> vos = new ArrayList<>();
            Map<String, String> moMaps = this.ticketService.getWeekdaysByMoTitleAndRegion(moTitle, region);
            Set<String> keys = new LinkedHashSet<>();
            SortedSet<String> thKeys = new TreeSet<>();
            for (MovieVo vo : movieVos) {
                keys.add(vo.getMoTitle());
                keys.add(vo.getMImgUrl());
                keys.add(vo.getRaGrade());
                keys.add(String.valueOf(vo.getTheaterCount()));
                keys.add(vo.getRegName());
                keys.add(String.valueOf(vo.getMoNum()));
                thKeys.add(vo.getThName());
            }
            vos.add(new Object[]{keys, thKeys, moMaps});
            modelAndView.addObject("movieRegVos", vos);
        }
        if (moTitle == null && thName != null && scStartDate == null) {
            MovieVo[] movieVos = this.ticketService.selectAllMoviesByThName(thName);
            List<Object[]> vos = new ArrayList<>();
            Map<String, String> moMaps = this.ticketService.getWeekdaysByThName(thName);
            Map<String, Boolean> map = new HashMap<>();
            for (MovieVo vo : movieVos) {
                switch (vo.getRaGrade()) {
                    case "청소년관람불가" -> vo.setRaGrade("nineteen");
                    case "15세이상관람가" -> vo.setRaGrade("fifteen");
                    case "12세이상관람가" -> vo.setRaGrade("twelve");
                    case "전체관람가" -> vo.setRaGrade("all");
                    case "미정" -> vo.setRaGrade("none");
                }
                String key = vo.getMoTitle() + "&&" + vo.getRaGrade();
                map.put(key, true);
            }
            vos.add(new Object[]{map, moMaps});
            modelAndView.addObject("theaterVos", vos);
        }
        if (moTitle == null && thName == null && scStartDate != null && region == null) {
            MovieVo[] movieVos = this.ticketService.selectAllMoviesByScStartDate(scStartDate);
            List<Object[]> vos = new ArrayList<>();
            SortedSet<String> thKeys = new TreeSet<>();
            Set<String> counts = new LinkedHashSet<>();
            Map<String, Boolean> map = new HashMap<>();
            for (MovieVo vo : movieVos) {
                switch (vo.getRaGrade()) {
                    case "청소년관람불가" -> vo.setRaGrade("nineteen");
                    case "15세이상관람가" -> vo.setRaGrade("fifteen");
                    case "12세이상관람가" -> vo.setRaGrade("twelve");
                    case "전체관람가" -> vo.setRaGrade("all");
                    case "미정" -> vo.setRaGrade("none");
                }
                String key = vo.getMoTitle() + "&&" + vo.getRaGrade();
                thKeys.add(vo.getThName());
                map.put(key, true);
                counts.add(String.valueOf(vo.getTheaterCount()));
                counts.add(vo.getRegName());
            }
            vos.add(new Object[]{map, thKeys, counts});
            modelAndView.addObject("dateVos", vos);
        }
        if (moTitle == null && thName == null && scStartDate != null && region != null) {
            MovieVo[] movieVos = this.ticketService.selectAllMoviesByScStartDateAndRegion(scStartDate, region);
            List<Object[]> vos = new ArrayList<>();
            SortedSet<String> thKeys = new TreeSet<>();
            Set<String> counts = new LinkedHashSet<>();
            Map<String, Boolean> map = new HashMap<>();
            for (MovieVo vo : movieVos) {
                switch (vo.getRaGrade()) {
                    case "청소년관람불가" -> vo.setRaGrade("nineteen");
                    case "15세이상관람가" -> vo.setRaGrade("fifteen");
                    case "12세이상관람가" -> vo.setRaGrade("twelve");
                    case "전체관람가" -> vo.setRaGrade("all");
                    case "미정" -> vo.setRaGrade("none");
                }
                String key = vo.getMoTitle() + "&&" + vo.getRaGrade();
                thKeys.add(vo.getThName());
                map.put(key, true);
                counts.add(String.valueOf(vo.getTheaterCount()));
                counts.add(vo.getRegName());
            }
            vos.add(new Object[]{map, thKeys, counts});
            modelAndView.addObject("dateRegionVos", vos);
        }
        if (moTitle != null && thName != null && scStartDate == null) {
            List<Object[]> vos = new ArrayList<>();
            Map<String, String> moMaps = this.ticketService.getWeekdaysByMoTitleAndThName(moTitle, thName);
            vos.add(new Object[]{moMaps});
            modelAndView.addObject("moThVos", vos);
        }
        if (moTitle != null && thName == null && scStartDate != null && region == null) {
            MovieVo[] movieVos = this.ticketService.selectAllMoviesByMoTitleAndScStartDate(moTitle, scStartDate);
            List<Object[]> vos = new ArrayList<>();
            SortedSet<String> thKeys = new TreeSet<>();
            Set<String> counts = new LinkedHashSet<>();
            for (MovieVo vo : movieVos) {
                thKeys.add(vo.getThName());
                counts.add(vo.getRegName());
                counts.add(String.valueOf(vo.getTheaterCount()));
            }
            vos.add(new Object[]{thKeys, counts});
            modelAndView.addObject("moScVos", vos);
        }
        if (moTitle != null && thName == null && scStartDate != null && region != null) {
            MovieVo[] movieVos = this.ticketService.selectAllMoviesByMoTitleAndScStartDateAndRegion(moTitle, scStartDate, region);
            List<Object[]> vos = new ArrayList<>();
            SortedSet<String> thKeys = new TreeSet<>();
            Set<String> counts = new LinkedHashSet<>();
            for (MovieVo vo : movieVos) {
                thKeys.add(vo.getThName());
                counts.add(vo.getRegName());
                counts.add(String.valueOf(vo.getTheaterCount()));
            }
            vos.add(new Object[]{thKeys, counts});
            modelAndView.addObject("moScRegionVos", vos);
        }
        if (moTitle == null && thName != null && scStartDate != null) {
            MovieVo[] movieVos = this.ticketService.selectAllMoviesByThNameAndScStartDate(thName, scStartDate);
            List<Object[]> vos = new ArrayList<>();
            Set<String> keys = new LinkedHashSet<>();
            Map<String, Boolean> map = new HashMap<>();
            for (MovieVo vo : movieVos) {
                switch (vo.getRaGrade()) {
                    case "청소년관람불가" -> vo.setRaGrade("nineteen");
                    case "15세이상관람가" -> vo.setRaGrade("fifteen");
                    case "12세이상관람가" -> vo.setRaGrade("twelve");
                    case "전체관람가" -> vo.setRaGrade("all");
                    case "미정" -> vo.setRaGrade("none");
                }
                String key = vo.getMoTitle() + "&&" + vo.getRaGrade();
                map.put(key, true);
            }
            vos.add(new Object[]{keys, map});
            modelAndView.addObject("thScVos", vos);
        }
        if (moTitle != null && thName != null && scStartDate != null) {
            MovieVo[] movieVos = this.ticketService.selectAllMovies(moTitle);
            ScreenVo[] screens = this.ticketService.selectScreenDatesByMovieAndTheaterAndDate(moTitle, thName, scStartDate);
            Map<List<String>, List<List<String>>> times = new LinkedHashMap<>();
            List<Object[]> vos = new ArrayList<>();
            Set<String> MoKeys = new LinkedHashSet<>();
            for (MovieVo vo : movieVos) {
                MoKeys.add(vo.getMoTitle());
                MoKeys.add(vo.getMImgUrl());
                MoKeys.add(vo.getRaGrade());
                MoKeys.add(String.valueOf(vo.getTheaterCount()));
                MoKeys.add(vo.getRegName());
                MoKeys.add(String.valueOf(vo.getMoNum()));
            }
            vos.add(new Object[]{MoKeys});
            for (ScreenVo screen : screens) {
                List<String> keys = new ArrayList<>();
                List<String> contents = new ArrayList<>();
                ScreenDataVo screenDataVo = new ScreenDataVo(screen.getSeatCount(), screen.getUsedSeatCount());
                keys.add(screen.getCitName());
                keys.add(screen.getCiName());
                keys.add(String.valueOf(screen.getSeatCount()));
                contents.add(String.valueOf(screen.getScStartDate()));
                contents.add(String.valueOf(screenDataVo.getEmptySeatCount()));
                times.computeIfAbsent(keys, k -> new ArrayList<>()).add(contents);
            }
            modelAndView.addObject("times", times);
            modelAndView.addObject("Vos", vos);
        }
        modelAndView.addObject("movies", movies);
        modelAndView.addObject("regions", regions);
        modelAndView.addObject("theaters", theaters);
        modelAndView.addObject("maps", maps);

        modelAndView.setViewName("ticket/index");
        return modelAndView;
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postSession(HttpSession session) {
        JSONObject response = new JSONObject();
        if (session.getAttribute("user") != null) {
            response.put(Result.RESULT, "success");
        } else {
            response.put(Result.RESULT, "failure");
        }
        return response.toString();
    }

    @RequestMapping(value = "/crawling", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCrawling() throws IOException, InterruptedException {
        ModelAndView modelAndView = new ModelAndView();
        this.ticketService.Crawl();
        modelAndView.setViewName("ticket/crawling");
        return modelAndView;
    }

    @RequestMapping(value = "/showTimes", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getShowTimes(@RequestParam(value = "region", required = false) String region,
                                     @RequestParam(value = "theater", required = false) String theater,
                                     @RequestParam(value = "movie", required = false) String movie,
                                     @RequestParam(value = "date", required = false) String date,
                                     @RequestParam(value = "cinema", required = false) String cinema) {
        ModelAndView modelAndView = new ModelAndView();
        MovieVo[] movies = this.ticketService.selectAllMoviesByRating();
        RegionEntity[] regions = this.theaterService.findRegionAll();
        TheaterEntity[] theaters = this.theaterService.getTheatersByRegion(region);
        if (theater != null) {
            TheaterVo[] theaterVos = this.theaterService.selectAllTheaters(theater);
            Map<String, String> maps = this.theaterService.getWeekdays(theater);
            Set<String> keys = new LinkedHashSet<>();
            List<Object[]> values = new ArrayList<>();
            Set<String> types = new LinkedHashSet<>();
            for (TheaterVo theaterVo : theaterVos) {
                keys.add(theaterVo.getThName());
                keys.add(theaterVo.getThAddr().split("\n")[0]);
                keys.add(theaterVo.getThAddr().split("\n")[1]);
                keys.add(theaterVo.getThImg());
                keys.add(String.valueOf(theaterVo.getSeatCount()));
                keys.add(String.valueOf(theaterVo.getCinemaCount()));
                keys.add(theaterVo.getThParking());
                if (theaterVo.getCitName().equals("4DX")) {
                    theaterVo.setCitName("DX");
                }
                types.add(theaterVo.getCitName());
            }
            values.add(new Object[]{keys, types, maps});
            modelAndView.addObject("theaterVos", values);
        }
        if (date != null && theater != null) {
            Map<Set<String>, Map<Set<String>, Set<String>>> screenVos = this.theaterService.selectAllScreens(date, theater);
            modelAndView.addObject("screenVos", screenVos);
        }
        if (region != null && movie != null) {
            TheaterVo[] theaterVos = this.theaterService.selectAllTheatersByRegion(region, movie);
            Map<String, String> maps = this.theaterService.getWeekdaysByRegion(region, movie);
            Map<Set<String>, Set<Set<String>>> map = this.ticketService.selectShowTimesByMoTitle(movie);
            Set<String> keys = new LinkedHashSet<>();
            List<Object[]> values = new ArrayList<>();
            Set<String> types = new LinkedHashSet<>();
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
            }
            values.add(new Object[]{keys, types, maps});
            modelAndView.addObject("theaterVos", values);
            modelAndView.addObject("map", map);
        }
        if (date != null && region != null && movie != null && cinema != null) {
            Map<Set<String>, Map<Set<String>, Set<String>>> screenVos = this.theaterService.selectAllScreensByCinemaType(date, region, movie, cinema);
            modelAndView.addObject("screenVos", screenVos);
        }
        modelAndView.addObject("regions", regions);
        modelAndView.addObject("theaters", theaters);
        modelAndView.addObject("movies", movies);
        modelAndView.setViewName("ticket/showTimes");
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
    public String postIndex(@SessionAttribute(value = "user") UserEntity user,
                            @RequestParam(value = "meName", required = false) String meName,
                            @RequestParam(value = "paPrice", required = false) int paPrice,
                            @RequestParam(value = "seName", required = false) String[] seNames,  // 여러 좌석 정보 배열로 받기
                            @RequestParam(value = "moTitle", required = false) String moTitle,
                            @RequestParam(value = "ciName", required = false) String ciName,
                            @RequestParam(value = "thName", required = false) String thName,
                            @RequestParam(value = "scStartDate", required = false) LocalDateTime scStartDate,
                            HttpSession session) {

        System.out.println("유저 정보 :" + user.getUsName());

//        if  (user.getUsNum() != usNum) {
//            int result = this.ticketService.selectPaymentNum(moTitle, ciName, thName, scStartDate, paPrice, usNum);
//            JSONObject response = new JSONObject();
//            response.put(Result.NAME, result);
//            return response.toString();

//        }
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
        // seNames 배열이 비어 있는지 체크
        if (seNames == null || seNames.length == 0) {
            throw new IllegalArgumentException("좌석 정보가 전달되지 않았습니다.");
        }

        // 결제 정보 저장
        Result result = this.ticketService.insertReservationAndPayment(loggedInUser.getUsNum(), meName, paPrice, seNames, moTitle, ciName, thName, scStartDate);

        int results = this.ticketService.selectPaymentNum(moTitle, ciName, thName, scStartDate, paPrice, loggedInUser.getUsNum());

        // 응답 데이터 생성
        JSONObject response = new JSONObject();



        response.put(Result.NAME, result.nameToLower());
        response.put(Result.NAMES, results);

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
