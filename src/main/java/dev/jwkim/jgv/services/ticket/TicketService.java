package dev.jwkim.jgv.services.ticket;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.entities.theater.CinemaEntity;
import dev.jwkim.jgv.entities.theater.CinemaTypeEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.entities.ticket.MethodEntity;
import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import dev.jwkim.jgv.exceptions.TransactionalException;
import dev.jwkim.jgv.mappers.ticket.MethodMapper;
import dev.jwkim.jgv.mappers.ticket.PaymentMapper;
import dev.jwkim.jgv.mappers.ticket.ReservationMapper;
import dev.jwkim.jgv.mappers.ticket.TicketMapper;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.vos.theater.MovieVo;
import dev.jwkim.jgv.vos.theater.RegionVo;
import dev.jwkim.jgv.vos.theater.ScreenVo;
import dev.jwkim.jgv.vos.ticket.CinemaTypeVo;
import dev.jwkim.jgv.vos.ticket.SeatVo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketMapper ticketMapper;
    private final MethodMapper methodMapper;
    private final PaymentMapper paymentMapper;
    private final ReservationMapper reservationMapper;

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
        return this.ticketMapper.selectAllMoviesByscStartDate(scStartDate);
    }

    public MovieVo[] selectAllMoviesByMoTitleAndScStartDate(String moTitle, String scStartDate) {
        if (moTitle == null || moTitle.isEmpty() ||
                scStartDate == null || scStartDate.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByMoTitleAndScStartDate(moTitle, scStartDate);
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

    public MovieVo[] selectAllMoviesByKorea() {
        MovieVo[] movies = this.ticketMapper.selectAllMoviesByKorea();
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
        DAEGU("CGV대구", "0345"),
        SUSEONG("CGV대구수성", "0157"),
        STADIUM("CGV대구스타디움", "0108"),
        ACADEMY("CGV대구아카데미", "0185"),
        YEONGGYEONG("CGV대구연경", "0343"),
        WOLSEONG("CGV대구월성", "0216"),
        JUKJEON("CGV대구죽전", "0256"),
        HANIL("CGV대구한일", "0147"),
        HYUNDAI("CGV대구현대", "0109");

        private final String cgvName;
        private final String cgvCode;

        TheaterCode(String cgvName, String cgvCode) {
            this.cgvName = cgvName;
            this.cgvCode = cgvCode;
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

    // region 크롤링
    @Transactional
    public void Crawl(ScreenEntity screen) throws TransactionalException {
        System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--headless");

        WebDriver driver = new ChromeDriver();

        try {
            for (TheaterCode theater : TheaterCode.values()) {
                int ciNum = 0;
                String dateUrl = "http://www.cgv.co.kr/theaters/?areacode=11&theaterCode=" + theater.cgvCode;
                driver.get(dateUrl);

                WebElement iframe = driver.findElement(By.id("ifrm_movie_time_table"));
                driver.switchTo().frame(iframe);

                List<WebElement> dateElements = driver.findElements(By.cssSelector("#slider > .item-wrap.on > .item > li"));
                List<String> dates = new ArrayList<>();
                for (WebElement day : dateElements) {
                    String movie = day.findElement(By.cssSelector("a")).getAttribute("href");
                    if (movie.isEmpty()) {
                        continue;
                    }

                    URL url = new URL(movie);
                    String query = url.getQuery();
                    if (query == null || query.isEmpty()) {
                        continue;
                    }

                    Map<String, String> queryParams = new HashMap<>();
                    String[] pairs = query.split("&");

                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=");
                        if (keyValue.length == 2) {
                            queryParams.put(keyValue[0], keyValue[1]);
                        }
                    }

                    String date = queryParams.get("date");
                    if (date != null) {
                        dates.add(date);
                    }
                }
                System.out.println(dates);
                System.out.println(theater.cgvName);

                for (int i = 0; i < dates.toArray().length; i++) {
                    String date = dates.toArray()[i].toString(); // YYYYMMDD 형식의 날짜
                    System.out.println("상영일: " + date);

                    String url = "http://www.cgv.co.kr/theaters/?areacode=11&theaterCode=" + theater.cgvCode + "&date=" + date;
                    driver.get(url);

                    WebElement iframes = driver.findElement(By.id("ifrm_movie_time_table"));
                    driver.switchTo().frame(iframes);

                    List<WebElement> movieElements = driver.findElements(By.cssSelector(".col-times"));

                    for (WebElement movieElement : movieElements) {
                        String movieTitle = movieElement.findElement(By.cssSelector(".info-movie > a > strong")).getText().trim();
                        MovieEntity movieNum = this.ticketMapper.selectMovieNumByMovieTitle(movieTitle);
                        if (movieNum == null) {
                            break;
                        }
                        screen.setMoNum(movieNum.getMoNum());

                        List<WebElement> timeTables = movieElement.findElements(By.cssSelector(".type-hall"));
                        StringBuilder timeTable = new StringBuilder();
                        for (WebElement table : timeTables) {
                            List<WebElement> cinemas = table.findElements(By.cssSelector(".info-hall > ul > li:nth-child(2)"));
                            for (WebElement cinema : cinemas) {
                                String result = "";
                                for (CinemaCode code : CinemaCode.values()) {
                                    if (cinema.getText().trim().contains("4DX관")) {
                                        result = "4DX";
                                        CinemaEntity cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(result, theater.cgvName);
                                        screen.setCiNum(cinemaTypeNum.getCiNum());
                                        ciNum = cinemaTypeNum.getCiNum();
                                        break;
                                    }
                                    if (cinema.getText().trim().contains("씨네앤포레")) {
                                        result = "CINE&FORET";
                                        CinemaEntity cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(result, theater.cgvName);
                                        screen.setCiNum(cinemaTypeNum.getCiNum());
                                        ciNum = cinemaTypeNum.getCiNum();
                                        break;
                                    }
                                    if (cinema.getText().contains("[CGV아트하우스]") ||
                                            cinema.getText().contains("[영남이공대학교]") ||
                                            cinema.getText().contains("[아트기획전관]")) {
                                        result = cinema.getText();
                                        CinemaEntity artCinema = this.ticketMapper.selectCinemaNumByCinemaTitle(result.substring(0, 2), theater.cgvName);
                                        screen.setCiNum(artCinema.getCiNum());
                                        ciNum = artCinema.getCiNum();
                                        break;
                                    }
                                    if (cinema.getText().contains("비상설")) {
                                        continue;
                                    }
                                    if (code.citName.equals(cinema.getText())) {
                                        result = cinema.getText().trim();
                                        CinemaEntity cinemaTypeNum = this.ticketMapper.selectCinemaNumByCinemaType(result, theater.cgvName);
                                        screen.setCiNum(cinemaTypeNum.getCiNum());
                                        ciNum = cinemaTypeNum.getCiNum();
                                        break;
                                    } else {
                                        screen.setCiNum(0);
                                    }
                                }
                                if (screen.getCiNum() == 0) {
                                    if (cinema.getText() != null && cinema.getText().length() >= 3) {
                                        result = cinema.getText().trim();
                                        CinemaEntity cinemaNum = this.ticketMapper.selectCinemaNumByCinemaTitle(result.substring(0, 3), theater.cgvName);
                                        screen.setCiNum(cinemaNum.getCiNum());
                                        ciNum = cinemaNum.getCiNum();
                                    } else if (cinema.getText() != null && cinema.getText().length() >= 2) {
                                        result = cinema.getText().trim();
                                        CinemaEntity cinemaNum = this.ticketMapper.selectCinemaNumByCinemaTitle(result.substring(0, 2), theater.cgvName);
                                        screen.setCiNum(cinemaNum.getCiNum());
                                        ciNum = cinemaNum.getCiNum();
                                    }
                                }
                                timeTable.append("상영관: ").append(result).append("\n");
                                List<WebElement> timeElements = table.findElements(By.cssSelector(".info-timetable > ul > li > a > em"));
                                for (WebElement element : timeElements) {
                                    timeTable.append("상영 시간: ").append(element.getText()).append("\n");
                                    String dateTimeString = date + "T" + element.getText();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HH:mm");
                                    screen.setScStartDate(LocalDateTime.parse(dateTimeString, formatter));
                                    ScreenEntity[] screens = this.ticketMapper.selectDuplicateScreen(LocalDateTime.parse(dateTimeString, formatter), this.ticketMapper.selectMovieNumByMovieTitle(movieTitle).getMoNum(), ciNum);
                                    if (screens.length < 1) {
                                        this.ticketMapper.insertScreen(screen);
                                    }
                                }
                            }
                        }
                        System.out.println("------------");
                        System.out.println("영화: " + movieTitle);
                        System.out.println(timeTable.toString().trim());
                    }
                    System.out.println("------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
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

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED) // 모든 예외에 대해 롤백
    public CommonResult insertPayment(String meName, int paPrice, int usNum) {
        // 결제 방법 번호 조회
        MethodEntity methodNum = this.methodMapper.selectPaymentMeNum(meName);

        if (methodNum == null) {
            System.out.println("결제 방법이 존재하지 않습니다: " + meName);
            return CommonResult.FAILURE;
        }

        // 유효성 검사 - 결제 금액과 사용자 번호 확인
        if (paPrice <= 0 || paPrice > 250_000) {
            System.out.println("유효하지 않은 결제 금액: " + paPrice);
            return CommonResult.FAILURE;
        }
        if (usNum <= 0) {
            System.out.println("유효하지 않은 사용자 번호: " + usNum);
            return CommonResult.FAILURE;
        }

        // PaymentEntity 객체 생성 및 값 설정
        PaymentEntity payment = new PaymentEntity();
        payment.setPaPrice(paPrice); // 결제 금액
        payment.setUsNum(usNum); // 사용자 번호
        payment.setMeNum(methodNum.getMeNum()); // 결제 방법 번호
        payment.setPaState(false); // 결제 상태 설정
        payment.setPaCreatedAt(LocalDateTime.now()); // 생성일 설정
        payment.setPaDeletedAt(null); // 삭제일 초기화

        // 유효성 검사 - 추가 필드 확인
        if (payment.getMeNum() <= 0 || payment.getMeNum() > 5) {
            System.out.println("유효하지 않은 결제 방법 번호: " + payment.getMeNum());
            return CommonResult.FAILURE;
        }

        // 결제 정보 DB 삽입
        int result = this.paymentMapper.insertPayment(payment);
        if (result <= 0) {
            System.out.println("결제 정보 삽입 실패: " + payment);
            return CommonResult.FAILURE;
        }

        System.out.println("결제 정보 삽입 성공: " + payment);
        return CommonResult.SUCCESS;
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED) // 모든 예외에 대해 롤백
    public CommonResult insertReservation(String moTitle, String ciName, String thName, LocalDateTime scStartDate, String meName, int usNum, String[] seNames) {
        // 영화 상영 정보 조회
        ScreenEntity[] screenEntities = this.reservationMapper.selectReservationByScNum(moTitle, ciName, thName, scStartDate);

        // 결제 정보 조회
        PaymentEntity[] paymentEntities = this.reservationMapper.selectPaymentByPaNum(meName, usNum);

        if (screenEntities == null) {
            System.out.println("영화 정보가 없습니다: " + moTitle + ", " + ciName + ", " + thName + ", " + scStartDate);
            return CommonResult.FAILURE;
        }

        if (paymentEntities == null) {
            System.out.println("결제 내역이 없습니다: " + meName + ", " + usNum);
            return CommonResult.FAILURE;
        }

        // 결제 정보의 상태를 업데이트할 PaymentEntity 가져오기
        PaymentEntity payment = paymentEntities[0]; // 첫 번째 결제 정보를 가져옴

        // 좌석 정보가 올바른지 확인
        if (seNames == null || seNames.length == 0) {
            System.out.println("좌석 정보가 잘못되었습니다.");
            return CommonResult.FAILURE;
        }

        try {
            // 여러 좌석에 대해 예약 처리
            for (String seName : seNames) {
                // 좌석 정보 조회
                SeatEntity[] seatEntities = this.reservationMapper.selectSeatBySeNum(seName, ciName, thName);

                if (seatEntities == null || seatEntities.length == 0) {
                    System.out.println("잘못된 좌석 정보: " + seName);
                    continue;  // 잘못된 좌석은 건너뛰고 계속 진행
                }

                // 예약 생성
                ReservationEntity reservation = new ReservationEntity();
                reservation.setScNum(screenEntities[0].getScNum());  // 상영 정보 설정
                reservation.setSeNum(seatEntities[0].getSeNum());  // 좌석 정보 설정
                reservation.setPaNum(payment.getPaNum());  // 결제 정보 설정

                // 예약 정보 저장
                this.reservationMapper.insertReservation(reservation);
                System.out.println("예약 성공! 좌석: " + seName);
            }

            // 모든 예약이 성공했으므로 결제 상태를 업데이트
            payment.setPaState(true);
            int updatedRows = this.paymentMapper.updatePaymentState(payment.getPaNum(), payment.isPaState());
            if (updatedRows <= 0) {
                System.out.println("결제 상태 업데이트 실패. PaNum: " + payment.getPaNum());
                return CommonResult.FAILURE;
            }

            return CommonResult.SUCCESS;
        } catch (Exception e) {
            System.err.println("예약 처리 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("예약 트랜잭션 실패", e); // 트랜잭션 롤백
        }
    }
}