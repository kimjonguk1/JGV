package dev.jwkim.jgv.services.Movie;

import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.entities.Movie.MovieEntity;
import dev.jwkim.jgv.mappers.Movie.GenreMapper;
import dev.jwkim.jgv.mappers.Movie.MovieImageMapper;
import dev.jwkim.jgv.mappers.Movie.MovieMapper;
import dev.jwkim.jgv.mappers.Movie.RaitingMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MovieService {
    private final MovieMapper movieMapper;
    private final MovieImageMapper movieImageMapper;
    private final RaitingMapper raitingMapper;
    private final GenreMapper genreMapper;

    public MovieService(MovieMapper movieMapper, MovieImageMapper movieImageMapper, RaitingMapper raitingMapper, GenreMapper genreMapper) {
        this.movieMapper = movieMapper;
        this.movieImageMapper = movieImageMapper;
        this.raitingMapper = raitingMapper;
        this.genreMapper = genreMapper;
    }

    //현재 상영장 크롤링 (수정중)
    public boolean insertmovie (MovieEntity movieEntity) {
        int affectRows = 0;
        int indata = this.movieMapper.selectMovie();
        if (indata > 0) {
            this.movieMapper.deleteAllMovieCharactor();
            this.movieMapper.deleteAllMovies();
            this.movieImageMapper.deleteAllMoviePosterUrl();
            this.raitingMapper.deleteAllMovieRaiting();
            this.genreMapper.deleteAllMovieGenre();
        }
            try {
                String url = "http://www.cgv.co.kr/movies/?lt=1&ft=1";
                Document movieDocument = Jsoup.connect(url).get();
                Elements movielinks = movieDocument.select("div.box-image > a");
                for (Element link : movielinks) {
                    System.out.println("반복");
                    String href = link.attr("href");
                    href = "http://www.cgv.co.kr" + href;
                    Document MovieDoc = Jsoup.connect(href).get();
                    // 영화 제목 가져오기
                    String MovieTitle = MovieDoc.select("div.box-contents > div.title > strong").text();
                    movieEntity.setMoTitle(MovieTitle);
                    // 영화 개봉일 가져오기
                    String MovieDate = MovieDoc.select("div.spec > dl > dt:contains(개봉) + dd").text();
                    if (MovieDate.contains("(")) {
                        MovieDate = MovieDate.split("\\(")[0].trim();
                    }
                    movieEntity.setMoDate(MovieDate);
                    // 영화 러닝타임, 장르, 관람등급, 제작국가 가져오기
                    String MovieRawData = MovieDoc.select("div.spec > dl > dt:contains(기본 정보) + dd").text();
                    // 러닝타임 가져오기
                    Pattern pattern = Pattern.compile("(\\d+)(?=분)");
                    Matcher matcher = pattern.matcher(MovieRawData);
                    if (matcher.find()) {
                        String minutes = matcher.group(1);
                        movieEntity.setMoTime(Integer.parseInt(minutes));
                    }

                    // 영화 줄거리 가져오기
                    String MoviePlot = MovieDoc.select("div.sect-story-movie").text();
                    movieEntity.setMoPlot(MoviePlot);
                    // 영화 예매율 가져오기
                    String MovieBooking = MovieDoc.select("strong.percent > span").text();
                    MovieBooking = MovieBooking.replaceAll("%", "").trim();
                    movieEntity.setMoBookingRate(Float.valueOf(MovieBooking));

//                    musicEntity.setCoverFileName($cover.attr("src"));

                    affectRows =  this.movieMapper.insertMovie(movieEntity);
                    int movieId = movieEntity.getMoNum();
                    // 영화 포스터 가져오기
                    Elements MoviePosterLink = MovieDoc.select("span.thumb-image > img");
                    String MoviePoster = MoviePosterLink.attr("src");
                    if (!MoviePoster.isEmpty()) {
                        this.movieImageMapper.insertMoviePosterUrl(movieId, MoviePoster);
                    }
                    // 관람 등급 가져오기
                    String[] parts = MovieRawData.split(",");
                    String raiting = parts[0].trim();
                    if(!raiting.isEmpty()) {
                        this.raitingMapper.insertMovieRaiting(movieId, raiting);
                    }
                    // 장르 가져오기
                    String MovieGenre = MovieDoc.select("dt:contains(장르)").text();
                    String genre = MovieGenre.replace("장르 :", "").trim();
                    if(!genre.isEmpty()) {
                        this.genreMapper.insertMovieGenre(genre);
                    }
                }

                //영화 링크 따기 (더보기 탭)
                String apiUrl = "http://www.cgv.co.kr/common/ajax/movies.aspx/GetMovieMoreList?listType=1&orderType=1&filterType=1";
                Connection.Response response = Jsoup.connect(apiUrl)
                        .header("Content-Type", "application/json; charset=utf-8")
                        .header("Accept", "application/json, text/javascript, */*; q=0.01")
                        .ignoreContentType(true)
                        .execute();

                JSONObject jsonObject = new JSONObject(response.body());
                String encodedData = jsonObject.getString("d");

                JSONObject dataObject = new JSONObject(encodedData);
                JSONArray movieList = dataObject.getJSONArray("List");

                for (int i = 0; i < movieList.length(); i++) {
                    JSONObject movie = movieList.getJSONObject(i);
                    int movieIdx = movie.getInt("MovieIdx");

                    String detailUrl = "http://www.cgv.co.kr/movies/detail-view/?midx=" + movieIdx;
                    Document MovieDoc = Jsoup.connect(detailUrl).get();
                    // 영화 제목 가져오기
                    String MovieTitle = MovieDoc.select("div.box-contents > div.title > strong").text();
                    movieEntity.setMoTitle(MovieTitle);
                    // 영화 개봉일 가져오기
                    String MovieDate = MovieDoc.select("div.spec > dl > dt:contains(개봉) + dd").text();
                    if (MovieDate.contains("(")) {
                        MovieDate = MovieDate.split("\\(")[0].trim();
                    }
                    movieEntity.setMoDate(MovieDate);
                    // 영화 러닝타임, 장르, 관람등급, 제작국가 가져오기
                    String MovieRawData = MovieDoc.select("div.spec > dl > dt:contains(기본 정보) + dd").text();
                    // 러닝타임 가져오기
                    Pattern pattern = Pattern.compile("(\\d+)(?=분)");
                    Matcher matcher = pattern.matcher(MovieRawData);
                    if (matcher.find()) {
                        String minutes = matcher.group(1);
                        movieEntity.setMoTime(Integer.parseInt(minutes));
                    }
                    // 영화 줄거리 가져오기
                    String MoviePlot = MovieDoc.select("div.sect-story-movie").text();
                    movieEntity.setMoPlot(MoviePlot);
                    // 영화 예매율 가져오기
                    String MovieBooking = MovieDoc.select("strong.percent > span").text();
                    MovieBooking = MovieBooking.replaceAll("%", "").trim();
                    movieEntity.setMoBookingRate(Float.valueOf(MovieBooking));

                    affectRows =  this.movieMapper.insertMovie(movieEntity);

                    int movieId = movieEntity.getMoNum();
                    // 영화 포스터 가져오기
                    Elements MoviePosterLink = MovieDoc.select("span.thumb-image > img");
                    String MoviePoster = MoviePosterLink.attr("src");
                    if (!MoviePoster.isEmpty()) {
                        this.movieImageMapper.insertMoviePosterUrl(movieId, MoviePoster);
                    }
                    // 관람 등급 가져오기
                    String[] parts = MovieRawData.split(",");
                    String raiting = parts[0].trim();
                    if(!raiting.isEmpty()) {
                        this.raitingMapper.insertMovieRaiting(movieId, raiting);
                    }
                    // 장르 가져오기
                    String MovieGenre = MovieDoc.select("dt:contains(장르)").text();
                    String genre = MovieGenre.replace("장르 :", "").trim();
                    if(!genre.isEmpty()) {
                        this.genreMapper.insertMovieGenre(genre);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        return affectRows > 0;
    }


    //상영 예정작 크롤링
    public boolean insertPreMovies(MovieEntity movieEntity) {
        int affectRows = 0;

        try {
            String url = "http://www.cgv.co.kr/movies/pre-movies.aspx";
            Document preMoviesDocument = Jsoup.connect(url).get();
            Elements preMovieLinks = preMoviesDocument.select("div.box-image > a");

            for (Element link : preMovieLinks) {
                String href = link.attr("href");
                href = "http://www.cgv.co.kr" + href;
                Document movieDoc = Jsoup.connect(href).get();

                // 영화 제목 가져오기
                String movieTitle = movieDoc.select("div.box-contents > div.title > strong").text();
                movieEntity.setMoTitle(movieTitle);

                // 영화 개봉일 가져오기
                String MovieDate = movieDoc.select("div.spec > dl > dt:contains(개봉) + dd").text();
                if (MovieDate.contains("(")) {
                    MovieDate = MovieDate.split("\\(")[0].trim();
                }
                movieEntity.setMoDate(MovieDate);

                // 영화 러닝타임, 장르, 관람등급, 제작국가 가져오기
                String movieRawData = movieDoc.select("div.spec > dl > dt:contains(기본 정보) + dd").text();
                Pattern pattern = Pattern.compile("(\\d+)(?=분)");
                Matcher matcher = pattern.matcher(movieRawData);
                if (matcher.find()) {
                    String minutes = matcher.group(1);
                    movieEntity.setMoTime(Integer.parseInt(minutes));
                }

                // 영화 줄거리 가져오기
                String moviePlot = movieDoc.select("div.sect-story-movie").text();
                movieEntity.setMoPlot(moviePlot);

                // 영화 예매율 가져오기
                String MovieBooking = movieDoc.select("strong.percent > span").text();
                MovieBooking = MovieBooking.replaceAll("%", "").trim();
                movieEntity.setMoBookingRate(Float.valueOf(MovieBooking));

                String MovieRawData = movieDoc.select("div.spec > dl > dt:contains(기본 정보) + dd").text();

                affectRows = this.movieMapper.insertMovie(movieEntity);
                int movieId = movieEntity.getMoNum();

                // 영화 포스터 가져오기
                Elements moviePosterLink = movieDoc.select("span.thumb-image > img");
                String moviePoster = moviePosterLink.attr("src");
                if (!moviePoster.isEmpty()) {
                    this.movieImageMapper.insertMoviePosterUrl(movieId, moviePoster);
                }

                // 관람 등급 가져오기
                String[] parts = MovieRawData.split(",");
                String raiting = parts[0].trim();
                if(!raiting.isEmpty()) {
                    this.raitingMapper.insertMovieRaiting(movieId, raiting);
                }
                // 장르 가져오기
                String MovieGenre = movieDoc.select("dt:contains(장르)").text();
                String genre = MovieGenre.replace("장르 :", "").trim();
                if(!genre.isEmpty()) {
                    this.genreMapper.insertMovieGenre(genre);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return affectRows > 0;
    }

    //중복 데이터 삭제
    public boolean deleteDupleMovies() {
        return this.movieMapper.deleteDupleMovies();
    }

    //현재 상영작, 상영 예정작 전부 크롤링
    public boolean insertAllMovies(MovieEntity movieEntity) {
        boolean currentMoviesInserted = insertmovie(movieEntity);
        boolean preMoviesInserted = insertPreMovies(movieEntity);
        boolean deleteDupleMovies = deleteDupleMovies();
        return currentMoviesInserted && preMoviesInserted && deleteDupleMovies;
    }

    public List<Movie_ImageDTO> selectAllMovieList() {
        return this.movieMapper.selectAllMovies();
    }

}
