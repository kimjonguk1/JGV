package dev.jwkim.jgv;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.entities.theater.CinemaEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.mappers.theater.TheaterMapper;
import dev.jwkim.jgv.mappers.ticket.TicketMapper;
import dev.jwkim.jgv.services.ticket.TicketService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//@SpringBootTest
@ActiveProfiles(value = "local")
public class CrawlTest {
//    @Autowired
//    private TheaterMapper theaterMapper;
//    @Autowired
//    private TicketMapper ticketMapper;

    private HttpResponse<String> getResponse(String areacode, String theaterCode, String date) throws IOException, InterruptedException {
        String sessionId = RandomStringUtils.randomAlphanumeric(24);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("http://www.cgv.co.kr/common/showtimes/iframeTheater.aspx?areacode=%s&theatercode=%s&date=%s", areacode, theaterCode, date)))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Accept-Encoding", "utf-8")
                .header("Accept-Language", "ko-KR,ko;q=0.9")
//                .header("Connection", "keep-alive")
                .header("Cookie", String.format("ASP.NET_SessionId=%s;", sessionId))
                .header("Referer", "http://www.cgv.co.kr/theaters/?areacode=11&theaterCode=0345")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

//    @Test
//    public void test() throws IOException, InterruptedException {
//        TheaterEntity[] theaters = this.theaterMapper.getAllTheaters();
//        Map<String, Map<String, CinemaEntity>> cinemaTypeMap = new HashMap<>();
//        Map<String, Map<String, CinemaEntity>> cinemaTitleMap = new HashMap<>();
//        for (TheaterEntity theater : theaters) {
//            TicketService.TheaterCode _tc = Arrays.stream(TicketService.TheaterCode.values())
//                    .filter((x) -> x.getCgvName().equals(theater.getThName()))
//                    .findFirst()
//                    .orElseThrow();
//            String areacode = _tc.getAreaCode();
//            String theaterCode = _tc.getCgvCode();
//            HttpResponse<String> response = this.getResponse(areacode, theaterCode, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
//            String responseBody = response.body();
//            Document document = Jsoup.parse(responseBody);
//            Elements items = document.select("#slider > .item-wrap.on > .item > li");
//            List<String> dates = new LinkedList<>();
//            for (Element item : items) {
//                Element $anchor;
//                if (($anchor = item.selectFirst("a")) == null || !$anchor.hasAttr("href")) {
//                    continue;
//                }
//                String[] queryArray = $anchor.attr("href").split("[?&]");
//                for (String query : queryArray) {
//                    String[] keyValue = query.split("=");
//                    if (keyValue.length == 2 && keyValue[0].equals("date")) {
//                        dates.add(keyValue[1]);
//                        break;
//                    }
//                }
//            }
//
//            for (String date : dates) {
//                response = this.getResponse(areacode, theaterCode, date);
//                responseBody = response.body();
//                document = Jsoup.parse(responseBody);
//                items = document.select(".col-times");
//                for (Element item : items) {
//                    Element $title;
//                    if (($title = item.selectFirst(".info-movie > a > strong")) == null) {
//                        continue;
//                    }
//                    String title = $title.text().trim();
//                    ScreenEntity screen = new ScreenEntity();
//                    MovieEntity movie = this.ticketMapper.selectMovieNumByMovieTitle(title);
//                    if (movie == null) {
//                        continue;
//                    }
//                    screen.setMoNum(movie.getMoNum());
//
//                    Elements $timetables = item.select(".type-hall");
//                    for (Element $timetable : $timetables) {
//                        Element $cinemas;
//                        if (($cinemas = $timetable.selectFirst(".info-hall > ul > li:nth-child(2)")) == null) {
//                            continue;
//                        }
//                        int cinemaNum = 0;
//                        String cinemaDerivedName = $cinemas.text().trim();
//                        String cinemaClearedName;
//                        CinemaEntity cinemaTypeNum;
//                        if (cinemaDerivedName.contains("비상설")) {
//                            continue;
//                        } else if (cinemaDerivedName.contains("4DX관")) {
//                            cinemaClearedName = "4DX";
//                        } else if (cinemaDerivedName.contains("씨네앤포레")) {
//                            cinemaClearedName = "CINE&FORET";
//                        } else if (cinemaDerivedName.contains("[CGV아트하우스]") ||
//                                cinemaDerivedName.contains("[영남이공대학교]") ||
//                                cinemaDerivedName.contains("[아트기획전관]")) {
//                            cinemaClearedName = cinemaDerivedName.substring(0, 2);
//                            cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaTitle(cinemaClearedName, theater.getThName());
//
//                            if (cinemaTypeNum != null) {
//                                cinemaNum = cinemaTypeNum.getCiNum();
//                            }
//                        } else {
//                            cinemaClearedName = Arrays.stream(TicketService.CinemaCode.values())
//                                    .map(TicketService.CinemaCode::getCitName)
//                                    .filter((x) -> x.equals(cinemaDerivedName))
//                                    .findFirst()
//                                    .orElse(null);
//                        }
//                        if (cinemaNum == 0) {
//                            if (cinemaTypeMap.containsKey(theater.getThName()) &&
//                                    cinemaTypeMap.get(theater.getThName()).containsKey(cinemaClearedName) &&
//                                    cinemaTypeMap.get(theater.getThName()).get(cinemaClearedName) != null) {
//                                cinemaTypeNum = cinemaTypeMap.get(theater.getThName()).get(cinemaClearedName);
//                            } else {
//                                if (cinemaClearedName == null) {
//                                    screen.setCiNum(0);
//                                    cinemaClearedName = cinemaDerivedName.substring(0, Math.min(cinemaDerivedName.length(), 3));
//                                    cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaTitle(cinemaClearedName, theater.getThName());
//                                } else {
//                                    cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(cinemaClearedName, theater.getThName());
//                                }
//                                if (!cinemaTypeMap.containsKey(theater.getThName())) {
//                                    cinemaTypeMap.put(theater.getThName(), new HashMap<>());
//                                }
//                                if (!cinemaTypeMap.get(theater.getThName()).containsKey(cinemaClearedName)) {
//                                    cinemaTypeMap.get(theater.getThName()).put(cinemaClearedName, cinemaTypeNum);
//                                }
//                            }
//
////                            if (cinemaClearedName == null) {
////                                screen.setCiNum(0);
////                                cinemaClearedName = cinemaDerivedName.substring(0, Math.min(cinemaDerivedName.length(), 3));
////
////                                cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaTitle(cinemaClearedName, theater.getThName());
////                            } else {
////                                if (cinemaTypeMap.containsKey(theater.getThName()) &&
////                                        cinemaTypeMap.get(theater.getThName()).containsKey(cinemaClearedName) &&
////                                        cinemaTypeMap.get(theater.getThName()).get(cinemaClearedName) != null) {
////                                    cinemaTypeNum = cinemaTypeMap.get(theater.getThName()).get(cinemaClearedName);
////                                } else {
////                                    cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(cinemaClearedName, theater.getThName());
////                                    if (!cinemaTypeMap.containsKey(theater.getThName())) {
////                                        cinemaTypeMap.put(theater.getThName(), new HashMap<>());
////                                    }
////                                    if (!cinemaTypeMap.get(theater.getThName()).containsKey(cinemaClearedName)) {
////                                        cinemaTypeMap.get(theater.getThName()).put(cinemaClearedName, cinemaTypeNum);
////                                    }
////                                }
////                            }
//                            screen.setCiNum(cinemaTypeNum.getCiNum());
//                            cinemaNum = cinemaTypeNum.getCiNum();
//                        }
//
//                        Elements $times = $timetable.select(".info-timetable > ul > li > a > em");
//                        for (Element $time : $times) {
//                            String updatedText;
//                            if (Integer.parseInt($time.text().substring(0, 2)) >= 24) {
//                                date = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).plusDays(1).toString().replaceAll("-", "");
//                                updatedText = String.format("%02d%s", Integer.parseInt($time.text().substring(0, 2)) - 24, $time.text().substring(2));
//                            } else {
//                                updatedText = $time.text();
//                            }
//                            String dateTimeString = date + "T" + updatedText;
//                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HH:mm");
//                            screen.setScStartDate(LocalDateTime.parse(dateTimeString, formatter));
//                            ScreenEntity[] screens = this.ticketMapper.selectDuplicateScreen(LocalDateTime.parse(dateTimeString, formatter), this.ticketMapper.selectMovieNumByMovieTitle(title).getMoNum(), cinemaNum);
//                            System.out.println("f: " + LocalDateTime.parse(dateTimeString, formatter));
//                            System.out.println("s: " + this.ticketMapper.selectMovieNumByMovieTitle(title).getMoNum());
//                            System.out.println("c : " + cinemaNum);
//                            if (screens.length < 1) {
//                                System.out.printf("%s - %s\n", title, screen.getScStartDate());
//                                this.ticketMapper.insertScreen(screen);
//                            } else {
//                                System.out.println("!!!");
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}
