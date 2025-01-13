package dev.jwkim.jgv.services.ticket;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.entities.theater.CinemaEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.exceptions.TransactionalException;
import dev.jwkim.jgv.mappers.theater.TheaterMapper;
import dev.jwkim.jgv.mappers.ticket.MethodMapper;
import dev.jwkim.jgv.mappers.ticket.PaymentMapper;
import dev.jwkim.jgv.mappers.ticket.ReservationMapper;
import dev.jwkim.jgv.mappers.ticket.TicketMapper;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.results.Result;
import dev.jwkim.jgv.results.reservation.ReservationResult;
import dev.jwkim.jgv.vos.theater.MovieVo;
import dev.jwkim.jgv.vos.theater.RegionVo;
import dev.jwkim.jgv.vos.theater.ScreenVo;
import dev.jwkim.jgv.vos.ticket.CinemaTypeVo;
import dev.jwkim.jgv.vos.ticket.SeatVo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketMapper ticketMapper;
    private final MethodMapper methodMapper;
    private final PaymentMapper paymentMapper;
    private final ReservationMapper reservationMapper;
    private final TheaterMapper theaterMapper;

    public Map<Set<String>, Set<Set<String>>> selectShowTimesByMoTitle(String movie) {
        MovieVo[] movies = this.ticketMapper.selectShowTimesByMoTitle(movie);
        Map<Set<String>, Set<Set<String>>> map = new LinkedHashMap<>();
        Set<String> keys = new LinkedHashSet<>();
        Set<Set<String>> values = new LinkedHashSet<>();
        Set<String> genres = new LinkedHashSet<>();
        Set<String> citNames = new LinkedHashSet<>();
        for (MovieVo movieVo : movies) {
            keys.add(movieVo.getMoTitle());
            keys.add(movieVo.getMoDate());
            keys.add(String.valueOf(movieVo.getMoTime()));
            keys.add(movieVo.getMImgUrl());
            keys.add(String.valueOf(movieVo.getMoNum()));
            genres.add(movieVo.getGeName());
            citNames.add(movieVo.getCitName());
        }
        values.add(genres);
        values.add(citNames);
        map.computeIfAbsent(keys, k -> new LinkedHashSet<>()).addAll(values);
        return map;
    }

    public ScreenVo[] selectScreenDatesByMovieAndTheaterAndDate(String moTitle, String thName, String scStartDate) {
        if (moTitle == null || moTitle.isEmpty() || moTitle.length() > 100 ||
                thName == null || thName.isEmpty() || thName.length() > 30 ||
                scStartDate == null || scStartDate.isEmpty() || scStartDate.length() > 10) {
            return null;
        }
        TheaterEntity theater = this.ticketMapper.selectTheater(thName);
        MovieEntity movie = this.ticketMapper.selectMovieNumByMovieTitle(moTitle);
        return this.ticketMapper.selectScreenDatesByMovieAndTheaterAndDate(movie.getMoNum(), theater.getThNum(), scStartDate);
    }

    public MovieVo[] selectAllMovies(String moTitle) {
        if (moTitle == null || moTitle.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByMoTitle(moTitle);
    }

    public MovieVo[] selectAllMoviesByRegion(String moTitle, String region) {
        if (moTitle == null || moTitle.isEmpty() ||
                region == null || region.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByMoTitleAndRegion(moTitle, region);
    }

    public MovieVo[] selectAllMoviesByThName(String thName) {
        if (thName == null || thName.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByThName(thName);
    }

    public MovieVo[] selectAllMoviesByScStartDate(String scStartDate) {
        if (scStartDate == null || scStartDate.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByScStartDate(scStartDate);
    }

    public MovieVo[] selectAllMoviesByScStartDateAndRegion(String scStartDate, String region) {
        if (scStartDate == null || scStartDate.isEmpty() ||
                region == null || region.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByScStartDateAndRegion(scStartDate, region);
    }

    public MovieVo[] selectAllMoviesByMoTitleAndScStartDate(String moTitle, String scStartDate) {
        if (moTitle == null || moTitle.isEmpty() ||
                scStartDate == null || scStartDate.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByMoTitleAndScStartDate(moTitle, scStartDate);
    }

    public MovieVo[] selectAllMoviesByMoTitleAndScStartDateAndRegion(String moTitle, String scStartDate, String region) {
        if (moTitle == null || moTitle.isEmpty() ||
                scStartDate == null || scStartDate.isEmpty() ||
                region == null || region.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByMoTitleAndScStartDateAndRegion(moTitle, scStartDate, region);
    }

    public MovieVo[] selectAllMoviesByThNameAndScStartDate(String thName, String scStartDate) {
        if (thName == null || thName.isEmpty() ||
                scStartDate == null || scStartDate.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByThNameAndScStartDate(thName, scStartDate);
    }

    public MovieVo[] selectAllMoviesByRating() {
        MovieVo[] movies = this.ticketMapper.selectAllMoviesByRating();
        for (MovieVo movie : movies) {
            switch (movie.getRaGrade()) {
                case "청소년관람불가" -> movie.setRaGrade("nineteen");
                case "15세이상관람가" -> movie.setRaGrade("fifteen");
                case "12세이상관람가" -> movie.setRaGrade("twelve");
                case "전체관람가" -> movie.setRaGrade("all");
                case "미정" -> movie.setRaGrade("none");
            }
        }
        return movies;
    }

    public RegionVo[] selectRegionAndTheaterCount() {
        return this.ticketMapper.selectRegionAndTheaterCount();
    }


    public Map<String, String> getWeekdaysByMoTitleAndThName(String moTitle, String thName) {
        // 화면의 시작 날짜들을 가져옴
        MovieVo[] screens = this.ticketMapper.selectAllMoviesByMoTitleAndThName(moTitle, thName);

        // 고유 날짜를 저장할 Set
        SortedSet<String> sortedSet = new TreeSet<>();

        // 날짜 리스트를 돌면서 고유 날짜만 저장
        for (MovieVo screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }

        Map<String, String> map = new TreeMap<>();
        for (String title : sortSet) {
            List<String> list = new ArrayList<>();
            for (String day : sortedSet) {
                if (day.contains(title)) {
                    LocalDate localDate = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    list.add(day.split("-")[2] + "-" + localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN).split("요일")[0]);
                }
            }
            map.put(title, list.toString().replace('[', ' ').replace(']', ' '));
        }
        return map;
    }

    public Map<String, String> getWeekdaysByMoTitle(String moTitle) {
        MovieVo[] screens = this.ticketMapper.selectAllMoviesByMoTitle(moTitle);
        SortedSet<String> sortedSet = new TreeSet<>();
        for (MovieVo screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }

        Map<String, String> map = new TreeMap<>();
        for (String title : sortSet) {
            List<String> list = new ArrayList<>();
            for (String day : sortedSet) {
                if (day.contains(title)) {
                    LocalDate localDate = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    list.add(day.split("-")[2] + "-" + localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN).split("요일")[0]);
                }
            }
            map.put(title, list.toString().replace('[', ' ').replace(']', ' '));
        }
        return map;
    }

    public Map<String, String> getWeekdaysByMoTitleAndRegion(String moTitle, String region) {
        MovieVo[] screens = this.ticketMapper.selectAllMoviesByMoTitleAndRegion(moTitle, region);
        SortedSet<String> sortedSet = new TreeSet<>();
        for (MovieVo screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }

        Map<String, String> map = new TreeMap<>();
        for (String title : sortSet) {
            List<String> list = new ArrayList<>();
            for (String day : sortedSet) {
                if (day.contains(title)) {
                    LocalDate localDate = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    list.add(day.split("-")[2] + "-" + localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN).split("요일")[0]);
                }
            }
            map.put(title, list.toString().replace('[', ' ').replace(']', ' '));
        }
        return map;
    }

    public Map<String, String> getWeekdaysByThName(String thName) {
        MovieVo[] screens = this.ticketMapper.selectAllMoviesByThName(thName);
        SortedSet<String> sortedSet = new TreeSet<>();
        for (MovieVo screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }

        Map<String, String> map = new TreeMap<>();
        for (String title : sortSet) {
            List<String> list = new ArrayList<>();
            for (String day : sortedSet) {
                if (day.contains(title)) {
                    LocalDate localDate = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    list.add(day.split("-")[2] + "-" + localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN).split("요일")[0]);
                }
            }
            map.put(title, list.toString().replace('[', ' ').replace(']', ' '));
        }
        return map;
    }

    public Map<String, String> getWeekdays() {
        ScreenEntity[] screens = this.ticketMapper.selectAllScreenDates();
        SortedSet<String> sortedSet = new TreeSet<>();
        for (ScreenEntity screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }

        Map<String, String> map = new TreeMap<>();
        for (String title : sortSet) {
            List<String> list = new ArrayList<>();
            for (String day : sortedSet) {
                if (day.contains(title)) {
                    LocalDate localDate = LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    list.add(day.split("-")[2] + "-" + localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN).split("요일")[0]);
                }
            }
            map.put(title, list.toString().replace('[', ' ').replace(']', ' '));
        }
        return map;
    }

    // region 크롤링을 위한 영화관 열거형
    @Getter
    public enum TheaterCode {
        DAEGU("CGV대구", "0345", "11"),
        SUSEONG("CGV대구수성", "0157", "11"),
        STADIUM("CGV대구스타디움", "0108", "11"),
        ACADEMY("CGV대구아카데미", "0185", "11"),
        YEONGGYEONG("CGV대구연경", "0343", "11"),
        WOLSEONG("CGV대구월성", "0216", "11"),
        JUKJEON("CGV대구죽전", "0256", "11"),
        HANIL("CGV대구한일", "0147", "11"),
        HYUNDAI("CGV대구현대", "0109", "11");

        private final String cgvName;
        private final String cgvCode;
        private final String areaCode;

        TheaterCode(String cgvName, String cgvCode, String areaCode) {
            this.cgvName = cgvName;
            this.cgvCode = cgvCode;
            this.areaCode = areaCode;
        }
    }
    // endregion

    // region 크롤링을 위한 영화관 타입 열거형
    @Getter
    public enum CinemaCode {
        NORMAL("2D"),
        IMAX("IMAX"),
        FOURDX("4DX"),
        SCREENX("SCREENX"),
        RECLINER("리클라이너"),
        CINE("CINE&FORET");

        private final String citName;

        CinemaCode(String citName) {
            this.citName = citName;
        }
    }
    // endregion

    private HttpResponse<String> getResponse(String areacode, String theaterCode, String date) throws IOException, InterruptedException {
        String sessionId = RandomStringUtils.randomAlphanumeric(24);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("http://www.cgv.co.kr/common/showtimes/iframeTheater.aspx?areacode=%s&theatercode=%s&date=%s", areacode, theaterCode, date)))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Accept-Encoding", "utf-8")
                .header("Accept-Language", "ko-KR,ko;q=0.9")
                .header("Cookie", String.format("ASP.NET_SessionId=%s;", sessionId))
                .header("Referer", "http://www.cgv.co.kr/theaters/?areacode=11&theaterCode=0345")
                .header("Upgrade-Insecure-Requests", "1")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Configuration
    @EnableScheduling
    public class SchedulerConfiguration {
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void Crawl() throws IOException, InterruptedException {
        TheaterEntity[] theaters = this.theaterMapper.getAllTheaters();
        Map<String, Map<String, CinemaEntity>> cinemaTypeMap = new HashMap<>();
        Map<String, Map<String, CinemaEntity>> cinemaTitleMap = new HashMap<>();
        for (TheaterEntity theater : theaters) {
            TheaterCode _tc = Arrays.stream(TheaterCode.values())
                    .filter((x) -> x.getCgvName().equals(theater.getThName()))
                    .findFirst()
                    .orElseThrow();
            String areacode = _tc.getAreaCode();
            String theaterCode = _tc.getCgvCode();
            HttpResponse<String> response = this.getResponse(areacode, theaterCode, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            String responseBody = response.body();
            Document document = Jsoup.parse(responseBody);
            Elements items = document.select("#slider > .item-wrap.on > .item > li");
            List<String> dates = new LinkedList<>();
            for (Element item : items) {
                Element $anchor;
                if (($anchor = item.selectFirst("a")) == null || !$anchor.hasAttr("href")) {
                    continue;
                }
                String[] queryArray = $anchor.attr("href").split("[?&]");
                for (String query : queryArray) {
                    String[] keyValue = query.split("=");
                    if (keyValue.length == 2 && keyValue[0].equals("date")) {
                        dates.add(keyValue[1]);
                        break;
                    }
                }
            }

            for (String date : dates) {
                response = this.getResponse(areacode, theaterCode, date);
                responseBody = response.body();
                document = Jsoup.parse(responseBody);
                items = document.select(".col-times");
                for (Element item : items) {
                    Element $title;
                    if (($title = item.selectFirst(".info-movie > a > strong")) == null) {
                        continue;
                    }
                    String title = $title.text().trim();
                    ScreenEntity screen = new ScreenEntity();
                    MovieEntity movie = this.ticketMapper.selectMovieNumByMovieTitle(title);
                    if (movie == null) {
                        continue;
                    }
                    screen.setMoNum(movie.getMoNum());

                    Elements $timetables = item.select(".type-hall");
                    for (Element $timetable : $timetables) {
                        Element $cinemas;
                        if (($cinemas = $timetable.selectFirst(".info-hall > ul > li:nth-child(2)")) == null) {
                            continue;
                        }
                        int cinemaNum = 0;
                        String cinemaDerivedName = $cinemas.text().trim();
                        String cinemaClearedName;
                        CinemaEntity cinemaTypeNum;
                        if (cinemaDerivedName.contains("비상설")) {
                            continue;
                        } else if (cinemaDerivedName.contains("4DX관")) {
                            cinemaClearedName = "4DX";
                        } else if (cinemaDerivedName.contains("씨네앤포레")) {
                            cinemaClearedName = "CINE&FORET";
                        } else if (cinemaDerivedName.contains("[CGV아트하우스]") ||
                                cinemaDerivedName.contains("[영남이공대학교]") ||
                                cinemaDerivedName.contains("[아트기획전관]")) {
                            cinemaClearedName = cinemaDerivedName.substring(0, 2);
                            cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaTitle(cinemaClearedName, theater.getThName());

                            if (cinemaTypeNum != null) {
                                cinemaNum = cinemaTypeNum.getCiNum();
                            }
                        } else {
                            cinemaClearedName = Arrays.stream(CinemaCode.values())
                                    .map(CinemaCode::getCitName)
                                    .filter((x) -> x.equals(cinemaDerivedName))
                                    .findFirst()
                                    .orElse(null);
                        }
                        if (cinemaNum == 0) {
                            if (cinemaTypeMap.containsKey(theater.getThName()) &&
                                    cinemaTypeMap.get(theater.getThName()).containsKey(cinemaClearedName) &&
                                    cinemaTypeMap.get(theater.getThName()).get(cinemaClearedName) != null) {
                                cinemaTypeNum = cinemaTypeMap.get(theater.getThName()).get(cinemaClearedName);
                            } else {
                                if (cinemaClearedName == null) {
                                    screen.setCiNum(0);
                                    cinemaClearedName = cinemaDerivedName.substring(0, Math.min(cinemaDerivedName.length(), 3));
                                    cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaTitle(cinemaClearedName, theater.getThName());
                                } else {
                                    cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(cinemaClearedName, theater.getThName());
                                }
                                if (!cinemaTypeMap.containsKey(theater.getThName())) {
                                    cinemaTypeMap.put(theater.getThName(), new HashMap<>());
                                }
                                if (!cinemaTypeMap.get(theater.getThName()).containsKey(cinemaClearedName)) {
                                    cinemaTypeMap.get(theater.getThName()).put(cinemaClearedName, cinemaTypeNum);
                                }
                            }

//                            if (cinemaClearedName == null) {
//                                screen.setCiNum(0);
//                                cinemaClearedName = cinemaDerivedName.substring(0, Math.min(cinemaDerivedName.length(), 3));
//
//                                cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaTitle(cinemaClearedName, theater.getThName());
//                            } else {
//                                if (cinemaTypeMap.containsKey(theater.getThName()) &&
//                                        cinemaTypeMap.get(theater.getThName()).containsKey(cinemaClearedName) &&
//                                        cinemaTypeMap.get(theater.getThName()).get(cinemaClearedName) != null) {
//                                    cinemaTypeNum = cinemaTypeMap.get(theater.getThName()).get(cinemaClearedName);
//                                } else {
//                                    cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(cinemaClearedName, theater.getThName());
//                                    if (!cinemaTypeMap.containsKey(theater.getThName())) {
//                                        cinemaTypeMap.put(theater.getThName(), new HashMap<>());
//                                    }
//                                    if (!cinemaTypeMap.get(theater.getThName()).containsKey(cinemaClearedName)) {
//                                        cinemaTypeMap.get(theater.getThName()).put(cinemaClearedName, cinemaTypeNum);
//                                    }
//                                }
//                            }

                            cinemaNum = cinemaTypeNum.getCiNum();
                            screen.setCiNum(cinemaNum);
                        } else {
                            screen.setCiNum(cinemaNum);
                        }

                        Elements $times = $timetable.select(".info-timetable > ul > li > a > em");
                        for (Element $time : $times) {
                            String updatedText;
                            if (Integer.parseInt($time.text().substring(0, 2)) >= 24) {
                                date = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).plusDays(1).toString().replaceAll("-", "");
                                updatedText = String.format("%02d%s", Integer.parseInt($time.text().substring(0, 2)) - 24, $time.text().substring(2));
                            } else {
                                updatedText = $time.text();
                            }
                            String dateTimeString = date + "T" + updatedText;
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HH:mm");
                            screen.setScStartDate(LocalDateTime.parse(dateTimeString, formatter));
                            ScreenEntity[] screens = this.ticketMapper.selectDuplicateScreen(LocalDateTime.parse(dateTimeString, formatter), this.ticketMapper.selectMovieNumByMovieTitle(title).getMoNum(), cinemaNum);
                            if (screens.length < 1) {
                                System.out.printf("%s - %s\n", title, screen.getScStartDate());
                                this.ticketMapper.insertScreen(screen);
                            }
                        }
                    }
                }
            }
        }
    }

    // region 크롤링
//    @Transactional
//    public void Crawl(ScreenEntity screen) throws TransactionalException {
//        // ChromeDriver 경로 설정
//        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver"); // chromedriver.exe 경로 지정
//
//        ChromeOptions options = new ChromeOptions();
////        options.addArguments("--headless"); // 헤드리스 모드
//        options.addArguments("--no-sandbox");  // 리눅스 환경에서 필요할 수 있음
//        options.addArguments("--disable-dev-shm-usage"); // 성능 최적화
//        options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 브라우저 감지 비활성화
//        options.addArguments("--disable-gpu"); // GPU 사용 안함
//        options.addArguments("--disable-extensions"); // 확장 프로그램 비활성화
//        options.addArguments("--disable-software-rasterizer");  // GPU 비활성화
//        options.addArguments("--start-maximized"); // 최대화된 창으로 시작
//        options.addArguments("--disable-infobars");
//        options.addArguments("--remote-debugging-port=9222");
//
//        // WebDriver 생성
//        WebDriver driver = new ChromeDriver(options);
//
//        try {
//            // 오늘 날짜 가져오기
//
//            for (TheaterCode theater : TheaterCode.values()) {
//                int ciNum = 0;
//                String dateUrl = "http://www.cgv.co.kr/theaters/?areacode=11&theaterCode=" + "0345";
//                driver.get(dateUrl);
//
//                // iframe 요소 찾기 및 전환
//                WebElement iframe = driver.findElement(By.id("ifrm_movie_time_table"));
//                driver.switchTo().frame(iframe);
//
//                List<WebElement> dateElements = driver.findElements(By.cssSelector("#slider > .item-wrap.on > .item > li"));
//                List<String> dates = new ArrayList<>();
//                for (WebElement day : dateElements) {
//                    // 영화 제목 추출
//                    String movie = day.findElement(By.cssSelector("a")).getAttribute("href");
//                    if (movie.isEmpty()) {
//                        continue;
//                    }
//                    // URL에서 쿼리 파라미터 추출
//
//                    URL url = new URL(movie);
//                    String query = url.getQuery();
//
//                    // 쿼리 파라미터가 없다면 건너뜁니다.
//                    if (query == null || query.isEmpty()) {
//                        continue;
//                    }
//
//                    // 쿼리 파라미터 분리
//                    Map<String, String> queryParams = new HashMap<>();
//                    String[] pairs = query.split("&");
//
//                    // 각 파라미터에 대해 처리
//                    for (String pair : pairs) {
//                        String[] keyValue = pair.split("=");
//
//                        // '='가 없거나 keyValue의 길이가 2가 아니라면 건너뜁니다.
//                        if (keyValue.length == 2) {
//                            queryParams.put(keyValue[0], keyValue[1]);
//                        }
//                    }
//
//                    // 'date' 파라미터 값 추출
//                    String date = queryParams.get("date");
//
//                    // date 출력
//                    if (date != null) {
//                        dates.add(date);
//                    }
//                }
////                System.out.println(dates);
////                System.out.println(theater.cgvName);
//
//                // 오늘을 기준으로 해당 영화관에 존재하는 날짜만 크롤링
//                for (int i = 0; i < dates.toArray().length; i++) {
//                    String date = dates.toArray()[i].toString(); // YYYYMMDD 형식의 날짜
////                    System.out.println("상영일: " + date);
//
//                    // URL에 날짜 파라미터 추가
//                    String url = "http://www.cgv.co.kr/theaters/?areacode=11&theaterCode=" + "0345" + "&date=" + date;
//                    // CGV 극장 URL 열기
//                    driver.get(url);
//
//                    // iframe 요소 찾기 및 전환
//                    WebElement iframes = driver.findElement(By.id("ifrm_movie_time_table"));
//                    driver.switchTo().frame(iframes);
//
//                    // 영화 시간표 요소 가져오기
//                    List<WebElement> movieElements = driver.findElements(By.cssSelector(".col-times"));
//
//                    for (WebElement movieElement : movieElements) {
//                        // 영화 제목 추출
//                        String movieTitle = movieElement.findElement(By.cssSelector(".info-movie > a > strong")).getText().trim();
//                        MovieEntity movieNum = this.ticketMapper.selectMovieNumByMovieTitle(movieTitle);
//                        if (movieNum == null) {
//                            break;
//                        }
//                        screen.setMoNum(movieNum.getMoNum());
//
//                        // 상영 시간표 추출
//                        List<WebElement> timeTables = movieElement.findElements(By.cssSelector(".type-hall"));
//                        StringBuilder timeTable = new StringBuilder();
//                        for (WebElement table : timeTables) {
//                            List<WebElement> cinemas = table.findElements(By.cssSelector(".info-hall > ul > li:nth-child(2)"));
//                            for (WebElement cinema : cinemas) {
//                                String result = "";
//                                for (CinemaCode code : CinemaCode.values()) {
//                                    if (cinema.getText().trim().contains("4DX관")) {
//                                        result = "4DX";
//                                        CinemaEntity cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(result, theater.cgvName);
//                                        screen.setCiNum(cinemaTypeNum.getCiNum());
//                                        ciNum = cinemaTypeNum.getCiNum();
//                                        break;
//                                    }
//                                    if (cinema.getText().trim().contains("씨네앤포레")) {
//                                        result = "CINE&FORET";
//                                        CinemaEntity cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(result, theater.cgvName);
//                                        screen.setCiNum(cinemaTypeNum.getCiNum());
//                                        ciNum = cinemaTypeNum.getCiNum();
//                                        break;
//                                    }
//                                    if (cinema.getText().contains("[CGV아트하우스]") ||
//                                            cinema.getText().contains("[영남이공대학교]") ||
//                                            cinema.getText().contains("[아트기획전관]")) {
//                                        result = cinema.getText();
//                                        CinemaEntity artCinema = this.ticketMapper.selectCinemaNumByCinemaTitle(result.substring(0, 2), theater.cgvName);
//                                        screen.setCiNum(artCinema.getCiNum());
//                                        ciNum = artCinema.getCiNum();
//                                        break;
//                                    }
//                                    if (cinema.getText().contains("비상설")) {
//                                        continue;
//                                    }
//                                    if (code.citName.equals(cinema.getText())) {
//                                        result = cinema.getText().trim();
//                                        CinemaEntity cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(result, theater.cgvName);
//                                        screen.setCiNum(cinemaTypeNum.getCiNum());
//                                        ciNum = cinemaTypeNum.getCiNum();
//                                        break;
//                                    } else {
//                                        screen.setCiNum(0);
//                                    }
//                                }
//                                if (screen.getCiNum() == 0) { // 조건에 맞는 값을 찾지 못한 경우 처리
//                                    if (cinema.getText() != null && cinema.getText().length() >= 3) {
//                                        result = cinema.getText().trim();
//                                        CinemaEntity cinemaNum = this.ticketMapper.selectCinemaNumByCinemaTitle(result.substring(0, 3), theater.cgvName);
//                                        screen.setCiNum(cinemaNum.getCiNum());
//                                        ciNum = cinemaNum.getCiNum();
//                                    } else if (cinema.getText() != null && cinema.getText().length() >= 2) {
//                                        result = cinema.getText().trim();
//                                        CinemaEntity cinemaNum = this.ticketMapper.selectCinemaNumByCinemaTitle(result.substring(0, 2), theater.cgvName);
//                                        screen.setCiNum(cinemaNum.getCiNum());
//                                        ciNum = cinemaNum.getCiNum();
//                                    }
//                                }
//                                timeTable.append("상영관: ").append(result).append("\n");
//                                List<WebElement> timeElements = table.findElements(By.cssSelector(".info-timetable > ul > li > a > em"));
//                                for (WebElement element : timeElements) {
//                                    String updatedText;
//                                    timeTable.append("상영 시간: ").append(element.getText()).append("\n");
//                                    if (Integer.parseInt(element.getText().substring(0, 2)) >= 24) {
//                                        date = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).plusDays(1).toString().replaceAll("-", "");
//                                        updatedText = element.getText().replace(element.getText().substring(0, 2), "00");
//                                    } else {
//                                        updatedText = element.getText();
//                                    }
//                                    String dateTimeString = date + "T" + updatedText;
//                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HH:mm");
//                                    screen.setScStartDate(LocalDateTime.parse(dateTimeString, formatter));
//                                    ScreenEntity[] screens = this.ticketMapper.selectDuplicateScreen(LocalDateTime.parse(dateTimeString, formatter), this.ticketMapper.selectMovieNumByMovieTitle(movieTitle).getMoNum(), ciNum);
//                                    if (screens.length < 1) {
//                                        this.ticketMapper.insertScreen(screen);
//                                    }
//                                }
//                            }
//                        }
//
//                        // 출력
////                        System.out.println("------------");
////                        System.out.println("영화: " + movieTitle);
////                        System.out.println(timeTable.toString().trim());
//                    }
////                    System.out.println("------------");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // 브라우저 닫기
//            driver.quit();
//        }
//    }

    // endregion

//    ---------------------------------------

    public SeatVo[] selectSeatByReservationNum(String ciName, String thName, String moTitle, LocalDateTime scStartDate) {

        SeatVo[] seatNum = this.ticketMapper.selectSeatByReservationSeNum(ciName, thName, moTitle, scStartDate);


        return seatNum;
    }

    public CinemaTypeVo[] selectSeatByCitPrice(String ciName, String thName, String moTitle, LocalDateTime scStartDate) {

        CinemaTypeVo[] citPrice = this.ticketMapper.selectSeatByCitPrice(ciName, thName, moTitle, scStartDate);
        return citPrice;
    }

    private List<String> checkSeatsAvailability(String[] seNames, String ciName, String thName, LocalDateTime scStartDate) {
        // 좌석 중 예약된 것이 없으면 seNames 배열을 반환, 예약된 좌석은 제외
        List<String> availableSeats = Arrays.stream(seNames)
                .filter(seName -> this.reservationMapper.isSeatAlreadyReserved(seName, ciName, thName, scStartDate) == 0)  // 예약되지 않은 좌석만 필터링
                .collect(Collectors.toList());

        return availableSeats;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Result insertReservationAndPayment(int usNum, String meName, int paPrice, String[] seNames, String moTitle, String ciName, String thName, LocalDateTime scStartDate) {
        try {

            if (usNum == 0) {
                return ReservationResult.FAILURE_UN_STEADY_LOGIN;
            }

            // Step 1: 영화 상영 정보 조회
            ScreenEntity[] screenEntities = this.reservationMapper.selectReservationByScNum(moTitle, ciName, thName, scStartDate);
            if (screenEntities == null || screenEntities.length == 0) {
                return CommonResult.FAILURE;  // 영화 상영 정보가 없을 경우
            }

            // Step 2: 기존 Payment 데이터 조회
            PaymentEntity[] existingPayments = this.reservationMapper.selectPaymentByPaNum(meName, usNum);

            // Step 3: Payment 삽입
            PaymentEntity newPayment = new PaymentEntity();
            newPayment.setPaPrice(paPrice);
            newPayment.setUsNum(usNum);
            newPayment.setMeNum(this.methodMapper.selectPaymentMeNum(meName).getMeNum());
            newPayment.setPaState(false);
            newPayment.setPaCreatedAt(LocalDateTime.now());
            newPayment.setPaDeletedAt(null);

            int paymentResult = this.paymentMapper.insertPayment(newPayment);
            if (paymentResult <= 0) {
                return CommonResult.FAILURE;  // 결제 정보 삽입 실패
            }

            // Step 4: Payment 삽입 후 갱신된 Payment 데이터 조회
            PaymentEntity[] updatedPayments = this.reservationMapper.selectPaymentByPaNum(meName, usNum);
            PaymentEntity insertedPayment = findNewPayment(existingPayments, updatedPayments);
            if (insertedPayment == null) {
                return CommonResult.FAILURE;  // 삽입된 결제 정보를 찾을 수 없을 경우
            }

            // Step 5: 좌석 유효성 검사 (중복되지 않은 좌석만 필터링)
            List<String> availableSeats = checkSeatsAvailability(seNames, ciName, thName, scStartDate);
            if (availableSeats.isEmpty()) {
                deletePaymentIfInserted(insertedPayment.getPaNum()); // 결제 정보 삭제
                return CommonResult.FAILURE;  // 모든 좌석이 이미 예약됨
            }

            // Step 6: Reservation 삽입
            for (String seName : availableSeats) {
                SeatEntity[] seatEntities = this.reservationMapper.selectSeatBySeNum(seName, ciName, thName);
                if (seatEntities == null || seatEntities.length == 0) {
                    continue; // 잘못된 좌석 정보
                }

                ReservationEntity reservation = new ReservationEntity();
                reservation.setScNum(screenEntities[0].getScNum());
                reservation.setSeNum(seatEntities[0].getSeNum());
                reservation.setPaNum(insertedPayment.getPaNum());

                int reservationResult = this.reservationMapper.insertReservation(reservation);
                if (reservationResult <= 0) {
                    return CommonResult.FAILURE;  // 예약 실패
                }
            }

            // Step 7: 결제 상태 업데이트
            insertedPayment.setPaState(true);
            int updateResult = this.paymentMapper.updatePaymentState(insertedPayment.getPaNum(), true);
            if (updateResult <= 0) {
                deletePaymentIfInserted(insertedPayment.getPaNum()); // 결제 정보 삭제
                return CommonResult.FAILURE;  // 결제 상태 업데이트 실패
            }

            return CommonResult.SUCCESS;  // 성공
        } catch (Exception e) {
            throw new RuntimeException("예약 및 결제 처리 중 오류", e);  // 예외 발생 시 트랜잭션 롤백
        }
    }


    // 기존 Payment와 새 Payment 비교하여 삽입된 Payment 찾기
    private PaymentEntity findNewPayment(PaymentEntity[] existingPayments, PaymentEntity[] updatedPayments) {
        Set<Integer> existingPaNums = Arrays.stream(existingPayments)
                .map(PaymentEntity::getPaNum)
                .collect(Collectors.toSet());

        for (PaymentEntity payment : updatedPayments) {
            if (!existingPaNums.contains(payment.getPaNum())) {
                return payment;
            }
        }
        return null;
    }

    // 결제 데이터 삭제 (필요한 경우)
    private void deletePaymentIfInserted(int paNum) {
        int result = this.paymentMapper.deletePayment(paNum);
        if (result > 0) {
//            System.out.println("결제 정보 삭제 성공. PaNum: " + paNum);
        } else {
//            System.out.println("결제 정보 삭제 실패. PaNum: " + paNum);
        }
    }

    public int selectPaymentNum(String moTitle, String ciName, String thName, LocalDateTime scStartDate, int paPrice, int usNum) {

        // selectPaymentNum을 호출하여 결제 번호 조회
        return this.paymentMapper.selectPaymentNum(moTitle, ciName, thName, scStartDate, paPrice, usNum);
    }

}