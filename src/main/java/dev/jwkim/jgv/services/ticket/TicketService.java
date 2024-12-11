package dev.jwkim.jgv.services.ticket;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.entities.theater.CinemaEntity;
import dev.jwkim.jgv.entities.theater.CinemaTypeEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import dev.jwkim.jgv.exceptions.TransactionalException;
import dev.jwkim.jgv.mappers.ticket.TicketMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketMapper ticketMapper;

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

        TheaterCode(String cgvName, String cgvNumber) {
            this.cgvName = cgvName;
            this.cgvCode = cgvNumber;
        }
    }

    @Getter
    public enum CinemaCode {
        NORMAL("2D"),
        IMAX("IMAX"),
        FOURDX("4DX"),
        SCREENX("SCRRENX"),
        RECLINER("리클라이너"),
        CINE("CINE&FORET");

        private final String citName;

        CinemaCode(String citName) {
            this.citName = citName;
        }
    }

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
            LocalDate currentDate = LocalDate.now();
            for (TheaterCode theater : TheaterCode.values()) {
                int ciNum = 0;
                System.out.println(theater.cgvName);

                // 15일 간의 날짜를 반복하며 크롤링
                for (int i = 0; i < 15; i++) {
                    String date = currentDate.plusDays(i).toString().replace("-", ""); // YYYYMMDD 형식의 날짜
                    System.out.println("상영일: " + date);

                    // URL에 날짜 파라미터 추가
                    String url = "http://www.cgv.co.kr/theaters/?areacode=11&theaterCode=" + theater.cgvCode + "&date=" + date;
                    // CGV 극장 URL 열기
                    driver.get(url);

                    // iframe 요소 찾기 및 전환
                    WebElement iframe = driver.findElement(By.id("ifrm_movie_time_table"));
                    driver.switchTo().frame(iframe);

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
                                    if (cinema.getText().contains("[CGV아트하우스]") || cinema.getText().contains("[영남이공대학교]")) {
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
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 브라우저 닫기
            driver.quit();
        }
    }

//    ---------------------------------------

    public ReservationEntity[] selectSeatByReservationNum(String ciName, String thName) {

        ReservationEntity[] seatNum = this.ticketMapper.selectSeatByReservationSeNum(ciName, thName);


        return seatNum;
    }

    public SeatEntity[] selectSeatBySeatName(String ciName, String thName) {

        SeatEntity[] seatName = this.ticketMapper.selectSeatBySeName(ciName, thName);
        return seatName;
    }

    public CinemaTypeEntity[] selectSeatByCitPrice(String ciName, String thName) {

        CinemaTypeEntity[] citPrice = this.ticketMapper.selectSeatByCitPrice(ciName, thName);
        return citPrice;
    }
}
