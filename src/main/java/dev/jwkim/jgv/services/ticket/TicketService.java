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

    // 시간 정보를 찾아오는 것.
    public ScreenVo[] selectScreenDatesByMovieAndTheaterAndDate(String moTitle, String thName, String scStartDate) {
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

    public MovieVo[] selectAllMoviesByscStartDate(String scStartDate) {
        if (scStartDate == null || scStartDate.isEmpty()) {
            return null;
        }
        return this.ticketMapper.selectAllMoviesByscStartDate(scStartDate);
    }

    public MovieVo[] selectAllMoviesBymoTitleAndscStartDate(String moTitle, String scStartDate) {
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
        // 결과
        // [2024-12-11, 2024-12-12, 2024-12-13, 2024-12-14, 2024-12-15, 2024-12-16, 2024-12-17, 2024-12-18, 2024-12-19, 2024-12-20, 2024-12-21, 2024-12-22, 2024-12-23, 2024-12-24, 2024-12-25, 2024-12-26]

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }
        // 결과
        // [2024-12]

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
        // 결과
        // 2024-12 [11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27]

        // 결과 반환
        return map;
    }


    public Map<String, String> getWeekdaysByMoTitle(String moTitle) {
        // 화면의 시작 날짜들을 가져옴
        MovieVo[] screens = this.ticketMapper.selectAllMoviesByMoTitle(moTitle);

        // 고유 날짜를 저장할 Set
        SortedSet<String> sortedSet = new TreeSet<>();

        // 날짜 리스트를 돌면서 고유 날짜만 저장
        for (MovieVo screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }
        // 결과
        // [2024-12-11, 2024-12-12, 2024-12-13, 2024-12-14, 2024-12-15, 2024-12-16, 2024-12-17, 2024-12-18, 2024-12-19, 2024-12-20, 2024-12-21, 2024-12-22, 2024-12-23, 2024-12-24, 2024-12-25, 2024-12-26]

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }
        // 결과
        // [2024-12]

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
        // 결과
        // 2024-12 [11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27]

        // 결과 반환
        return map;
    }

    public Map<String, String> getWeekdaysByThName(String thName) {
        // 화면의 시작 날짜들을 가져옴
        MovieVo[] screens = this.ticketMapper.selectAllMoviesByThName(thName);

        // 고유 날짜를 저장할 Set
        SortedSet<String> sortedSet = new TreeSet<>();

        // 날짜 리스트를 돌면서 고유 날짜만 저장
        for (MovieVo screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }
        // 결과
        // [2024-12-11, 2024-12-12, 2024-12-13, 2024-12-14, 2024-12-15, 2024-12-16, 2024-12-17, 2024-12-18, 2024-12-19, 2024-12-20, 2024-12-21, 2024-12-22, 2024-12-23, 2024-12-24, 2024-12-25, 2024-12-26]

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }
        // 결과
        // [2024-12]

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
        // 결과
        // 2024-12 [11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27]

        // 결과 반환
        return map;
    }

    public Map<String, String> getWeekdays() {
        // 화면의 시작 날짜들을 가져옴
        ScreenEntity[] screens = this.ticketMapper.selectAllScreenDates();

        // 고유 날짜를 저장할 Set
        SortedSet<String> sortedSet = new TreeSet<>();

        // 날짜 리스트를 돌면서 고유 날짜만 저장
        for (ScreenEntity screen : screens) {
            sortedSet.add(screen.getScStartDate().toString().split("T")[0]);
        }
        // 결과
        // [2024-12-11, 2024-12-12, 2024-12-13, 2024-12-14, 2024-12-15, 2024-12-16, 2024-12-17, 2024-12-18, 2024-12-19, 2024-12-20, 2024-12-21, 2024-12-22, 2024-12-23, 2024-12-24, 2024-12-25, 2024-12-26]

        SortedSet<String> sortSet = new TreeSet<>();
        for (String sort : sortedSet) {
            sortSet.add(sort.substring(0, 7));
        }
        // 결과
        // [2024-12]

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
        // 결과
        // 2024-12 [11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27]

        // 결과 반환
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

    // region 열거형 예시
//    public class EnumExample {
//        public static void main(String[] args) {
//            // enum을 for-each 문으로 반복
//            for (TheaterCode color : TheaterCode.values()) {
//                System.out.println(color.name() + " " + color.ordinal());
//            }
//        }
//    }
    // endregion

    // region 크롤링
    @Transactional
    public void Crawl(ScreenEntity screen) throws TransactionalException {
        // ChromeDriver 경로 설정
        System.setProperty("webdriver.chrome.driver", "./chromedriver.exe"); // chromedriver.exe 경로 지정

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 브라우저 감지 비활성화
        options.addArguments("--headless"); // 브라우저 창을 띄우지 않고 실행 (옵션)

        // WebDriver 생성
        WebDriver driver = new ChromeDriver();

        try {
            // 오늘 날짜 가져오기

            for (TheaterCode theater : TheaterCode.values()) {
                int ciNum = 0;
                String dateUrl = "http://www.cgv.co.kr/theaters/?areacode=11&theaterCode=" + theater.cgvCode;
                driver.get(dateUrl);

                // iframe 요소 찾기 및 전환
                WebElement iframe = driver.findElement(By.id("ifrm_movie_time_table"));
                driver.switchTo().frame(iframe);

                List<WebElement> dateElements = driver.findElements(By.cssSelector("#slider > .item-wrap.on > .item > li"));
                List<String> dates = new ArrayList<>();
                for (WebElement day : dateElements) {
                    // 영화 제목 추출
                    String movie = day.findElement(By.cssSelector("a")).getAttribute("href");
                    if (movie.isEmpty()) {
                        continue;
                    }
                    // URL에서 쿼리 파라미터 추출
                    URL url = new URL(movie);
                    String query = url.getQuery();

                    // 쿼리 파라미터가 없다면 건너뜁니다.
                    if (query == null || query.isEmpty()) {
                        continue;
                    }

                    // 쿼리 파라미터 분리
                    Map<String, String> queryParams = new HashMap<>();
                    String[] pairs = query.split("&");

                    // 각 파라미터에 대해 처리
                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=");

                        // '='가 없거나 keyValue의 길이가 2가 아니라면 건너뜁니다.
                        if (keyValue.length == 2) {
                            queryParams.put(keyValue[0], keyValue[1]);
                        }
                    }

                    // 'date' 파라미터 값 추출
                    String date = queryParams.get("date");

                    // date 출력
                    if (date != null) {
                        dates.add(date);
                    }
                }
                System.out.println(dates);
                System.out.println(theater.cgvName);

                // 오늘을 기준으로 해당 영화관에 존재하는 날짜만 크롤링
                for (int i = 0; i < dates.toArray().length; i++) {
                    String date = dates.toArray()[i].toString(); // YYYYMMDD 형식의 날짜
                    System.out.println("상영일: " + date);

                    // URL에 날짜 파라미터 추가
                    String url = "http://www.cgv.co.kr/theaters/?areacode=11&theaterCode=" + theater.cgvCode + "&date=" + date;
                    // CGV 극장 URL 열기
                    driver.get(url);

//                    // iframe 요소 찾기 및 전환
                    WebElement iframes = driver.findElement(By.id("ifrm_movie_time_table"));
                    driver.switchTo().frame(iframes);

                    // 영화 시간표 요소 가져오기
                    List<WebElement> movieElements = driver.findElements(By.cssSelector(".col-times"));

                    for (WebElement movieElement : movieElements) {
                        // 영화 제목 추출
                        String movieTitle = movieElement.findElement(By.cssSelector(".info-movie > a > strong")).getText().trim();
                        MovieEntity movieNum = this.ticketMapper.selectMovieNumByMovieTitle(movieTitle);
                        if (movieNum == null) {
                            break;
                        }
                        screen.setMoNum(movieNum.getMoNum());

                        // 상영 시간표 추출
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
                                if (screen.getCiNum() == 0) { // 조건에 맞는 값을 찾지 못한 경우 처리
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

                        // 출력
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
            // 브라우저 닫기
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

    @Transactional // 트랜잭션 처리 (성공 시 커밋, 실패 시 롤백)
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

    @Transactional // 트랜잭션 처리 (성공 시 커밋, 실패 시 롤백)
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
                reservation.setPaNum(paymentEntities[0].getPaNum());  // 결제 정보 설정

                // 예약 정보 저장
                this.reservationMapper.insertReservation(reservation);
                System.out.println("예약 성공! 좌석: " + seName);
            }

            return CommonResult.SUCCESS;
        } catch (Exception e) {
            System.err.println("예약 처리 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("예약 트랜잭션 실패", e); // 트랜잭션 롤백
        }
    }
}