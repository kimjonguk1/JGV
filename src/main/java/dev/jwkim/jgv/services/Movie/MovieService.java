package dev.jwkim.jgv.services.Movie;

import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.entities.Movie.*;
import dev.jwkim.jgv.mappers.Movie.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MovieService {
    private final MovieMapper movieMapper;
    private final MovieImageMapper movieImageMapper;
    private final RaitingMapper raitingMapper;
    private final GenreMapper genreMapper;
    private final CountryMapper countryMapper;
    private final CharactorMapper charactorMapper;

    // 병렬 처리를 위한 스레드 풀
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public MovieService(MovieMapper movieMapper, MovieImageMapper movieImageMapper, RaitingMapper raitingMapper,
                        GenreMapper genreMapper, CountryMapper countryMapper, CharactorMapper charactorMapper) {
        this.movieMapper = movieMapper;
        this.movieImageMapper = movieImageMapper;
        this.raitingMapper = raitingMapper;
        this.genreMapper = genreMapper;
        this.countryMapper = countryMapper;
        this.charactorMapper = charactorMapper;
    }

    public boolean insertAllMovies(MovieEntity movieEntity) {
        try {
            // 기존 데이터 삭제
            // 스레드 동기화 : 여러 스레드가 동시에 접근하는 것을 막아줌
            synchronized (this) {
                clearDatabase();
            }

            // 병렬로 크롤링 및 삽입 작업 수행
            CompletableFuture<Void> currentMoviesFuture = CompletableFuture.runAsync(() -> insertMovies(movieEntity), executorService);
            CompletableFuture<Void> upcomingMoviesFuture = CompletableFuture.runAsync(() -> insertPreMovies(movieEntity), executorService);
            CompletableFuture<Void> moreMoviesFuture = CompletableFuture.runAsync(() -> crawlMoreMovies(movieEntity), executorService);

            // 모든 작업 완료 대기
            CompletableFuture.allOf(currentMoviesFuture, upcomingMoviesFuture, moreMoviesFuture).join();

            // 중복 데이터 제거
            synchronized (this) {
                deleteDupleMovies();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 모든 데이터 삭제
    private void clearDatabase() {
        try {
            raitingMapper.deleteAllMovieRaiting();

            charactorMapper.deleteAllMovieCharacterMapping();
            charactorMapper.deleteAllCharacterImg();
            charactorMapper.deleteAllCharacter();

            countryMapper.deleteAllCountryMapping();
            countryMapper.deleteAllCountry();

            genreMapper.deleteMovieGenreMapping();
            genreMapper.deleteAllMovieGenre();

            movieImageMapper.deleteAllMoviePosterUrl();
            movieMapper.deleteAllMovies();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertMovies(MovieEntity movieEntity) {
        try {
            String url = "http://www.cgv.co.kr/movies/?lt=1&ft=1";
            Document movieDocument = Jsoup.connect(url).timeout(10000).get();
            Elements movieLinks = movieDocument.select("div.box-image > a");

            for (Element link : movieLinks) {
                String href = "http://www.cgv.co.kr" + link.attr("href");
                synchronized (this) {
                    processMoviePage(movieEntity, href);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertPreMovies(MovieEntity movieEntity) {
        try {
            String url = "http://www.cgv.co.kr/movies/pre-movies.aspx";
            Document preMoviesDocument = Jsoup.connect(url).timeout(10000).get();
            Elements preMovieLinks = preMoviesDocument.select("div.box-image > a");

            for (Element link : preMovieLinks) {
                String href = "http://www.cgv.co.kr" + link.attr("href");
                synchronized (this) {
                    processMoviePage(movieEntity, href);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crawlMoreMovies(MovieEntity movieEntity) {
        try {
            String apiUrl = "http://www.cgv.co.kr/common/ajax/movies.aspx/GetMovieMoreList?listType=1&orderType=1&filterType=1";
            String response = Jsoup.connect(apiUrl)
                    .timeout(10000)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .ignoreContentType(true)
                    .execute()
                    .body();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray movieList = new JSONObject(jsonObject.getString("d")).getJSONArray("List");

            for (int i = 0; i < movieList.length(); i++) {
                JSONObject movie = movieList.getJSONObject(i);
                String detailUrl = "http://www.cgv.co.kr/movies/detail-view/?midx=" + movie.getInt("MovieIdx");
                synchronized (this) {
                    processMoviePage(movieEntity, detailUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void processMoviePage(MovieEntity movieEntity, String url) {
        try {
            Document movieDoc = Jsoup.connect(url).timeout(10000).get();
            String rawData = movieDoc.select("div.spec > dl > dt:contains(기본 정보) + dd").text();

            // 관람 등급
            String raiting = rawData.split("[,.]")[0].trim();
            RatingEntity ratingEntity = new RatingEntity();
            ratingEntity.setRaGrade(raiting);
            Integer raId = raitingMapper.selectRaitingIdByName(raiting);
            if (raId == null) {
                raitingMapper.insertMovieRaiting(ratingEntity);
                raId = ratingEntity.getRaNum();
            }

            // 영화 데이터 가져오기
            String movieTitle = movieDoc.select("div.box-contents > div.title > strong").text();
            movieEntity.setMoTitle(movieTitle);

            String movieDate = movieDoc.select("div.spec > dl > dt:contains(개봉) + dd").text();
            movieEntity.setMoDate(movieDate.split("\\(")[0].trim());


            Matcher matcher = Pattern.compile("(\\d+)(?=분)").matcher(rawData);
            if (matcher.find()) {
                movieEntity.setMoTime(Integer.parseInt(matcher.group(1)));
            }

            String moviePlot = movieDoc.select("div.sect-story-movie").text();
            movieEntity.setMoPlot(moviePlot);

            String bookingRate = movieDoc.select("strong.percent > span").text().replace("%", "").trim();
            movieEntity.setMoBookingRate(Float.valueOf(bookingRate));

            // 삽입 작업
            movieMapper.insertMovie(movieEntity, raId);

            // 영화 포스터
            String posterUrl = movieDoc.select("span.thumb-image > img").attr("src");
            if (!posterUrl.isEmpty()) {
                movieImageMapper.insertMoviePosterUrl(movieEntity.getMoNum(), posterUrl);
            }

            // 장르
            String MovieGenre = movieDoc.select("dt:contains(장르)").text();
            String genre = MovieGenre.replace("장르 :", "").trim();
            String[] genreArray = genre.split(",");
            for (String genreName : genreArray) {
                String trimmedGenre = genreName.trim(); // 공백 제거
                // 장르가 이미 존재하는지 확인
                Integer genreId = genreMapper.selectGenereIdByName(trimmedGenre);
                if (genreId == null) {
                    // 존재하지 않으면 새로 삽입
                    GenereEntity genereEntity = new GenereEntity();
                    genereEntity.setGeName(trimmedGenre);
                    genreMapper.insertMovieGenre(genereEntity);
                    genreId = genereEntity.getGeNum(); // 삽입된 ID 가져오기
                }
                // 영화와 장르 매핑
                genreMapper.insertMovieGenreMapping(movieEntity.getMoNum(), genreId);
            }

            // 제작 국가
            String[] parts = rawData.split("[,.]");
            for (String part : parts) {
                if (exceptPattern(part.trim())) {
                    CountryEntity countryEntity = new CountryEntity();
                    countryEntity.setCoName(part.trim());
                    Integer countryId = countryMapper.selectCountryIdByName(part.trim());
                    if (countryId == null) {
                        countryMapper.insertMovieCountry(countryEntity);
                        countryId = countryEntity.getCoNum();
                    }
                    countryMapper.insertMovieCountryMapping(movieEntity.getMoNum(), countryId);
                }
            }

            // 인물 데이터
            insertCharacterData(movieEntity.getMoNum(), url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertCharacterData(int movieId, String movieUrl) {
        try {
            // 캐릭터 상세 페이지 URL 구성
            String midx = movieUrl.split("\\?midx=")[1].split("#")[0];
            String characterUrl = "http://www.cgv.co.kr/movies/detail-view/cast.aspx?midx=" + midx + "#menu";
            Document characterDoc = Jsoup.connect(characterUrl).timeout(10000).get();
            // 감독 데이터 처리
            Elements directorLinks = characterDoc.select("div.sect-staff-director > ul > li > div.box-image > a");
            for (Element directorLink : directorLinks) {
                processCharacter(movieId, directorLink, "감독");
            }

            // 배우 데이터 처리
            Elements actorLinks = characterDoc.select("div.sect-staff-actor > ul > li > div.box-image > a");
            for (Element actorLink : actorLinks) {
                processCharacter(movieId, actorLink, "배우");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 개별 캐릭터 데이터를 처리하는 메서드
    private void processCharacter(int movieId, Element characterLink, String job) {
        try {
            // 캐릭터 상세 정보 페이지로 이동
            String href = "http://www.cgv.co.kr" + characterLink.attr("href");
            Document characterDoc = Jsoup.connect(href).timeout(10000).get();

            // 캐릭터 정보 크롤링
            String name = characterDoc.select("div.box-contents > div.title > strong").text();
            String birthRaw = characterDoc.select("dt:contains(출생) + dd").text();
            String nation = characterDoc.select("dt:contains(국적) + dd").text();

            // 캐릭터 엔티티 생성
            CharactorEntity charactorEntity = new CharactorEntity();
            charactorEntity.setChName(name);
            charactorEntity.setChJob(job);
            charactorEntity.setChNation(nation.isEmpty() ? null : nation);
            if (!birthRaw.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                    charactorEntity.setChBirth(LocalDate.parse(birthRaw, formatter));
                } catch (Exception e) {
                    charactorEntity.setChBirth(null); // 유효하지 않은 경우 null로 처리
                }
            }

            // 데이터베이스에 캐릭터 삽입 (존재하지 않을 경우)
            Integer charId = charactorMapper.selectCharacterIdByName(name);
            if (charId == null) {
                charactorMapper.insertCharacter(charactorEntity);
                charId = charactorEntity.getChNum();
            }

            // 캐릭터 이미지 처리
            String characterImage = characterDoc.select("div.box-image > a > span.thumb-image > img").attr("src");
            if (!characterImage.isEmpty()) {
                charactorMapper.insertCharacterImg(charId, characterImage);
            }

            // 영화와 캐릭터 매핑
            charactorMapper.insertMovieCharacter(movieId, charId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean exceptPattern(String part) {
        if (part == null || part.trim().isEmpty()) {
            return false;
        }
        return !part.matches(".*(\\d|분|관람|미정|청소년관람불가|전체관람가).*");
    }

    private synchronized boolean deleteDupleMovies() {
        return movieMapper.deleteDupleMovies();
    }

    public List<Movie_ImageDTO> selectAllMovieList() {
        return this.movieMapper.selectAllMovies();
    }
}
