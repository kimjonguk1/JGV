package dev.jwkim.jgv.services.Movie;

import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.entities.Movie.CharactorEntity;
import dev.jwkim.jgv.entities.Movie.CountryEntity;
import dev.jwkim.jgv.entities.Movie.GenereEntity;
import dev.jwkim.jgv.entities.Movie.MovieEntity;
import dev.jwkim.jgv.mappers.Movie.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class MovieService {
    private final MovieMapper movieMapper;
    private final MovieImageMapper movieImageMapper;
    private final RaitingMapper raitingMapper;
    private final GenreMapper genreMapper;
    private final CountryMapper countryMapper;
    private final CharactorMapper charactorMapper;

    public MovieService(MovieMapper movieMapper, MovieImageMapper movieImageMapper, RaitingMapper raitingMapper, GenreMapper genreMapper, CountryMapper countryMapper, CharactorMapper charactorMapper) {
        this.movieMapper = movieMapper;
        this.movieImageMapper = movieImageMapper;
        this.raitingMapper = raitingMapper;
        this.genreMapper = genreMapper;
        this.countryMapper = countryMapper;
        this.charactorMapper = charactorMapper;
    }

    //패턴을 제외하기 위한 메서드
    private static boolean exceptPattern(String Part) {
        return !Part.matches(".*(\\d|분|관람|미정|청소년관람불가|전체관람가).*");
    }
    //현재 상영장 크롤링
    public boolean insertmovie (MovieEntity movieEntity) {
        int movieid = 0;
        int indata = this.movieMapper.selectMovie();
        if (indata > 0) {
            this.movieMapper.deleteAllMovieCharactor();
            this.movieMapper.deleteAllMovies();
            this.movieImageMapper.deleteAllMoviePosterUrl();
            this.raitingMapper.deleteAllMovieRaiting();
            this.genreMapper.deleteAllMovieGenre();
            this.genreMapper.deleteMovieGenreMapping();
            this.countryMapper.deleteAllCountry();
            this.countryMapper.deleteAllCountryMapping();
            this.charactorMapper.deleteAllCharacter();
            this.charactorMapper.deleteAllCharacterImg();
            this.charactorMapper.deleteAllMovieCharacterMapping();
            //delete charactor -> charactor_image -> movie_charactor
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
                    //region 영화제목
                    String MovieTitle = MovieDoc.select("div.box-contents > div.title > strong").text();
                    movieEntity.setMoTitle(MovieTitle);
                    //endregion
                    //region 개봉일
                    String MovieDate = MovieDoc.select("div.spec > dl > dt:contains(개봉) + dd").text();
                    if (MovieDate.contains("(")) {
                        MovieDate = MovieDate.split("\\(")[0].trim();
                    }
                    movieEntity.setMoDate(MovieDate);
                    //endregion
                    //region 러닝타임, 장르, 관람등급, 제작국가 데이터
                    String MovieRawData = MovieDoc.select("div.spec > dl > dt:contains(기본 정보) + dd").text();
                    //endregion
                    //region 러닝타임
                    Pattern pattern = Pattern.compile("(\\d+)(?=분)");
                    Matcher matcher = pattern.matcher(MovieRawData);
                    if (matcher.find()) {
                        String minutes = matcher.group(1);
                        movieEntity.setMoTime(Integer.parseInt(minutes));
                    }
                    //endregion
                    //region 줄거리
                    String MoviePlot = MovieDoc.select("div.sect-story-movie").text();
                    movieEntity.setMoPlot(MoviePlot);
                    //endregion
                    //region 예매율
                    String MovieBooking = MovieDoc.select("strong.percent > span").text();
                    MovieBooking = MovieBooking.replaceAll("%", "").trim();
                    movieEntity.setMoBookingRate(Float.valueOf(MovieBooking));
                    //endregion
                    movieid =  this.movieMapper.insertMovie(movieEntity);
                    int movieId = movieEntity.getMoNum();
                    //region 포스터
                    Elements MoviePosterLink = MovieDoc.select("span.thumb-image > img");
                    String MoviePoster = MoviePosterLink.attr("src");
                    if (!MoviePoster.isEmpty()) {
                        if(movieId > 0) {
                            this.movieImageMapper.insertMoviePosterUrl(movieId, MoviePoster);
                        }
                    }
                    //endregion
                    //region 관람 등급
                    String[] parts = MovieRawData.split("[,.]\\s*");
                    String raiting = parts[0].trim();
                    if(!raiting.isEmpty()) {
                        this.raitingMapper.insertMovieRaiting(movieId, raiting);
                    }
                    //endregion
                    //region 장르
                    String MovieGenre = MovieDoc.select("dt:contains(장르)").text();
                    String genre = MovieGenre.replace("장르 :", "").trim();
                    GenereEntity genereEntity = new GenereEntity();
                    genereEntity.setGeName(genre);
                    Integer genum = this.genreMapper.selectGenereIdByName(genre);
                    if (genum == null) {
                        this.genreMapper.insertMovieGenre(genereEntity);
                        genum = genereEntity.getGeNum();
                    }
                    this.genreMapper.insertMovieGenreMapping(movieId, genum);
                    //endregion
                    //region 제작국가
                    for(String part : parts) {
                        String cleanPart = part.trim();
                        if(!cleanPart.isEmpty() && exceptPattern(cleanPart)) {
                            CountryEntity countryEntity = new CountryEntity();
                            countryEntity.setCoName(cleanPart);
                            Integer conum = this.countryMapper.selectCountryIdByName(cleanPart);
                            if(conum == null) {
                                this.countryMapper.insertMovieCountry(countryEntity);
                                conum = countryEntity.getCoNum();
                            }
                            this.countryMapper.insertMovieCountryMapping(movieId, conum);
                        }
                    }
                    //endregion
                    //region 인물
                    int questionMarkIndex = href.indexOf('?');
                    String NewchUrl = href.substring(questionMarkIndex);
                    String chUrl = "http://www.cgv.co.kr/movies/detail-view/cast.aspx" + NewchUrl + "#menu";
                    Document CharacterDoc = Jsoup.connect(chUrl).get();
                    Elements DirectorLinks = CharacterDoc.select("div.sect-staff-director > ul > li > div.box-image > a");
                    Elements CharacterLinks = CharacterDoc.select("div.sect-staff-actor > ul > li > div.box-image > a");
                    for (Element DirectorLink : DirectorLinks) {
                        // 감독 자체가 없을 수도 있어서 try catch 사용
                        try {
                            String DirectorHref = DirectorLink.attr("href");
                            DirectorHref = "http://www.cgv.co.kr" + DirectorHref;
                            Document DirectorDocs = Jsoup.connect(DirectorHref).get();
                            //감독 이름 가져오기
                            String DirectorName = DirectorDocs.select("div.box-contents > div.title > strong").text();
                            // 감독 출생 가져오기
                            String DirectorBirthRaw = DirectorDocs.select("dt:contains(출생) + dd").text();
                            LocalDate DirectorBirth = null;
                            if (!DirectorBirthRaw.isEmpty()) {
                                try {
                                    DirectorBirth = LocalDate.parse(DirectorBirthRaw); // 유효한 날짜만 변환
                                } catch (Exception e) {
                                    DirectorBirth = null;
                                }
                            }
                            // 감독 국적 가져오기
                            String DirectorNation = DirectorDocs.select("dt:contains(국적) + dd").text();
                            if (DirectorNation.isEmpty()) {
                                DirectorNation = null;
                            }
                            CharactorEntity charactorEntity = new CharactorEntity();
                            System.out.println(DirectorName);
                            System.out.println(DirectorBirth);
                            System.out.println(DirectorNation);
                            charactorEntity.setChName(DirectorName);
                            charactorEntity.setChBirth(DirectorBirth);
                            charactorEntity.setChNation(DirectorNation);
                            charactorEntity.setChJob("감독");
                            Integer ChNum = this.charactorMapper.selectCharacterIdByName(DirectorName);
                            if(ChNum == null) {
                                this.charactorMapper.insertCharacter(charactorEntity);
                                ChNum = charactorEntity.getChNum();
                            }
                            //감독 이미지 가져오기
                            Elements DirectorImgLink = DirectorDocs.select("div.sect-base > div.box-image > a > span.thumb-image > img");
                            String DirectorImg = DirectorImgLink.attr("src");
                            if(movieId > 0) {
                                this.charactorMapper.insertCharacterImg(ChNum, DirectorImg);
                            }
                            this.charactorMapper.insertMovieCharacter(movieId, ChNum);
                        } catch (Exception e) {
                            System.out.println("페이지가 존재하지 않습니다.");
                        }
                    }
                    for (Element CharacterLink : CharacterLinks) {
                        // 배우 자체가 없을 수도 있어서 try catch 사용
                        try {
                            String CharacterHref = CharacterLink.attr("href");
                            CharacterHref = "http://www.cgv.co.kr" + CharacterHref;
                            Document CharacterDocs = Jsoup.connect(CharacterHref).get();
                            //배우 이름 가져오기
                            String CharacterName = CharacterDocs.select("div.box-contents > div.title > strong").text();
                            // 배우 출생 가져오기
                            String CharacterBirthRaw = CharacterDocs.select("dt:contains(출생) + dd").text();
                            LocalDate CharacterBirth = null;
                            if (!CharacterBirthRaw.isEmpty()) {
                                try {
                                    CharacterBirth = LocalDate.parse(CharacterBirthRaw); // 유효한 날짜만 변환
                                } catch (Exception e) {
                                    CharacterBirth = null;
                                }
                            }
                            // 배우 국적 가져오기
                            String CharacterNation = CharacterDocs.select("dt:contains(국적) + dd").text();
                            if (CharacterNation.isEmpty()) {
                                CharacterNation = null;
                            }
                            CharactorEntity charactorEntity = new CharactorEntity();
                            charactorEntity.setChName(CharacterName);
                            charactorEntity.setChBirth(CharacterBirth);
                            charactorEntity.setChNation(CharacterNation);
                            charactorEntity.setChJob("배우");
                            Integer ChNum = this.charactorMapper.selectCharacterIdByName(CharacterName);
                            if(ChNum == null) {
                                this.charactorMapper.insertCharacter(charactorEntity);
                                ChNum = charactorEntity.getChNum();
                            }
                            //배우 이미지 가져오기
                            Elements CharacterImgLink = CharacterDocs.select("div.sect-base > div.box-image > a > span.thumb-image > img");
                            String CharacterImg = CharacterImgLink.attr("src");
                            if(movieId > 0) {
                                this.charactorMapper.insertCharacterImg(ChNum, CharacterImg);
                            }
                            this.charactorMapper.insertMovieCharacter(movieId, ChNum);
                        } catch (Exception e) {
                            System.out.println("페이지가 존재하지 않습니다.");
                        }
                    }
                    //endregion
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
                    //region 영화 제목
                    String MovieTitle = MovieDoc.select("div.box-contents > div.title > strong").text();
                    movieEntity.setMoTitle(MovieTitle);
                    //endregion
                    //region 개봉일
                    String MovieDate = MovieDoc.select("div.spec > dl > dt:contains(개봉) + dd").text();
                    if (MovieDate.contains("(")) {
                        MovieDate = MovieDate.split("\\(")[0].trim();
                    }
                    movieEntity.setMoDate(MovieDate);
                    //endregion
                    //region 러닝타임, 장르, 관람등급, 제작국가
                    String MovieRawData = MovieDoc.select("div.spec > dl > dt:contains(기본 정보) + dd").text();
                    //endregion
                    //region 러닝타임
                    Pattern pattern = Pattern.compile("(\\d+)(?=분)");
                    Matcher matcher = pattern.matcher(MovieRawData);
                    if (matcher.find()) {
                        String minutes = matcher.group(1);
                        movieEntity.setMoTime(Integer.parseInt(minutes));
                    }
                    //endregion
                    //region 줄거리
                    String MoviePlot = MovieDoc.select("div.sect-story-movie").text();
                    movieEntity.setMoPlot(MoviePlot);
                    //endregion
                    //region 예매율
                    String MovieBooking = MovieDoc.select("strong.percent > span").text();
                    MovieBooking = MovieBooking.replaceAll("%", "").trim();
                    movieEntity.setMoBookingRate(Float.valueOf(MovieBooking));
                    //endregion
                    movieid =  this.movieMapper.insertMovie(movieEntity);
                    int movieId = movieEntity.getMoNum();
                    //region 포스터
                    Elements MoviePosterLink = MovieDoc.select("span.thumb-image > img");
                    String MoviePoster = MoviePosterLink.attr("src");
                    if (!MoviePoster.isEmpty()) {
                        if(movieId > 0) {
                            this.movieImageMapper.insertMoviePosterUrl(movieId, MoviePoster);
                        }
                    }
                    //endregion
                    //region 관람 등급
                    String[] parts = MovieRawData.split("[,.]\\s*");
                    String raiting = parts[0].trim();
                    if(!raiting.isEmpty()) {
                        this.raitingMapper.insertMovieRaiting(movieId, raiting);
                    }
                    //endregion
                    //region 장르
                    String MovieGenre = MovieDoc.select("dt:contains(장르)").text();
                    String genre = MovieGenre.replace("장르 :", "").trim();
                    GenereEntity genereEntity = new GenereEntity();
                    genereEntity.setGeName(genre);
                    Integer genum = this.genreMapper.selectGenereIdByName(genre);
                    if (genum == null) {
                        this.genreMapper.insertMovieGenre(genereEntity);
                        genum = genereEntity.getGeNum();
                    }
                    this.genreMapper.insertMovieGenreMapping(movieId, genum);
                    //endregion
                    //region 제작국가
                    for(String part : parts) {
                        String cleanPart = part.trim();
                        if(!cleanPart.isEmpty() && exceptPattern(cleanPart)) {
                            CountryEntity countryEntity = new CountryEntity();
                            countryEntity.setCoName(cleanPart);
                            Integer conum = this.countryMapper.selectCountryIdByName(cleanPart);
                            if(conum == null) {
                                this.countryMapper.insertMovieCountry(countryEntity);
                                conum = countryEntity.getCoNum();
                            }
                            this.countryMapper.insertMovieCountryMapping(movieId, conum);
                        }
                    }
                    //endregion
                    //region 인물
                    String chUrl = "http://www.cgv.co.kr/movies/detail-view/cast.aspx?midx=" + movieIdx + "#menu";
                    Document CharacterDoc = Jsoup.connect(chUrl).get();
                    Elements DirectorLinks = CharacterDoc.select("div.sect-staff-director > ul > li > div.box-image > a");
                    Elements CharacterLinks = CharacterDoc.select("div.sect-staff-actor > ul > li > div.box-image > a");
                    for (Element DirectorLink : DirectorLinks) {
                        // 감독 자체가 없을 수도 있어서 try catch 사용
                        try {
                            String DirectorHref = DirectorLink.attr("href");
                            DirectorHref = "http://www.cgv.co.kr" + DirectorHref;
                            Document DirectorDocs = Jsoup.connect(DirectorHref).get();
                            //감독 이름 가져오기
                            String DirectorName = DirectorDocs.select("div.box-contents > div.title > strong").text();
                            // 감독 출생 가져오기
                            String DirectorBirthRaw = DirectorDocs.select("dt:contains(출생) + dd").text();
                            LocalDate DirectorBirth = null;
                            if (!DirectorBirthRaw.isEmpty()) {
                                try {
                                    DirectorBirth = LocalDate.parse(DirectorBirthRaw); // 유효한 날짜만 변환
                                } catch (Exception e) {
                                    DirectorBirth = null;
                                }
                            }
                            // 감독 국적 가져오기
                            String DirectorNation = DirectorDocs.select("dt:contains(국적) + dd").text();
                            if (DirectorNation.isEmpty()) {
                                DirectorNation = null;
                            }
                            CharactorEntity charactorEntity = new CharactorEntity();
                            charactorEntity.setChName(DirectorName);
                            charactorEntity.setChBirth(DirectorBirth);
                            charactorEntity.setChNation(DirectorNation);
                            charactorEntity.setChJob("감독");
                            Integer ChNum = this.charactorMapper.selectCharacterIdByName(DirectorName);
                            if(ChNum == null) {
                                this.charactorMapper.insertCharacter(charactorEntity);
                                ChNum = charactorEntity.getChNum();
                            }
                            //감독 이미지 가져오기
                            Elements DirectorImgLink = DirectorDocs.select("div.sect-base > div.box-image > a > span.thumb-image > img");
                            String DirectorImg = DirectorImgLink.attr("src");
                            if(movieId > 0) {
                                this.charactorMapper.insertCharacterImg(ChNum, DirectorImg);
                            }
                            this.charactorMapper.insertMovieCharacter(movieId, ChNum);
                        } catch (Exception e) {
                            System.out.println("페이지가 존재하지 않습니다.");
                        }
                    }
                    for (Element CharacterLink : CharacterLinks) {
                        // 배우 자체가 없을 수도 있어서 try catch 사용
                        try {
                            String CharacterHref = CharacterLink.attr("href");
                            CharacterHref = "http://www.cgv.co.kr" + CharacterHref;
                            Document CharacterDocs = Jsoup.connect(CharacterHref).get();
                            //배우 이름 가져오기
                            String CharacterName = CharacterDocs.select("div.box-contents > div.title > strong").text();
                            // 배우 출생 가져오기
                            String CharacterBirthRaw = CharacterDocs.select("dt:contains(출생) + dd").text();
                            LocalDate CharacterBirth = null;
                            if (!CharacterBirthRaw.isEmpty()) {
                                try {
                                    CharacterBirth = LocalDate.parse(CharacterBirthRaw); // 유효한 날짜만 변환
                                } catch (Exception e) {
                                    CharacterBirth = null;
                                }
                            }
                            // 배우 국적 가져오기
                            String CharacterNation = CharacterDocs.select("dt:contains(국적) + dd").text();
                            if (CharacterNation.isEmpty()) {
                                CharacterNation = null;
                            }
                            CharactorEntity charactorEntity = new CharactorEntity();
                            charactorEntity.setChName(CharacterName);
                            charactorEntity.setChBirth(CharacterBirth);
                            charactorEntity.setChNation(CharacterNation);
                            charactorEntity.setChJob("배우");
                            Integer ChNum = this.charactorMapper.selectCharacterIdByName(CharacterName);
                            if(ChNum == null) {
                                this.charactorMapper.insertCharacter(charactorEntity);
                                ChNum = charactorEntity.getChNum();
                            }
                            //배우 이미지 가져오기
                            Elements CharacterImgLink = CharacterDocs.select("div.sect-base > div.box-image > a > span.thumb-image > img");
                            String CharacterImg = CharacterImgLink.attr("src");
                            if(movieId > 0) {
                                this.charactorMapper.insertCharacterImg(ChNum, CharacterImg);
                            }
                            this.charactorMapper.insertMovieCharacter(movieId, ChNum);
                        } catch (Exception e) {
                            System.out.println("페이지가 존재하지 않습니다.");
                        }
                    }
                    //endregion
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        return movieid > 0;
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

                //region 영화 제목
                String movieTitle = movieDoc.select("div.box-contents > div.title > strong").text();
                movieEntity.setMoTitle(movieTitle);
                //endregion
                //region 영화 개봉일
                String MovieDate = movieDoc.select("div.spec > dl > dt:contains(개봉) + dd").text();
                if (MovieDate.contains("(")) {
                    MovieDate = MovieDate.split("\\(")[0].trim();
                }
                movieEntity.setMoDate(MovieDate);
                //endregion
                //region 러닝타임, 장르, 관람등급, 제작국가
                String movieRawData = movieDoc.select("div.spec > dl > dt:contains(기본 정보) + dd").text();
                Pattern pattern = Pattern.compile("(\\d+)(?=분)");
                Matcher matcher = pattern.matcher(movieRawData);
                if (matcher.find()) {
                    String minutes = matcher.group(1);
                    movieEntity.setMoTime(Integer.parseInt(minutes));
                }
                //endregion
                //region 줄거리
                String moviePlot = movieDoc.select("div.sect-story-movie").text();
                movieEntity.setMoPlot(moviePlot);
                //endregion
                //region 예매율
                String MovieBooking = movieDoc.select("strong.percent > span").text();
                MovieBooking = MovieBooking.replaceAll("%", "").trim();
                movieEntity.setMoBookingRate(Float.valueOf(MovieBooking));
                //endregion

                affectRows = this.movieMapper.insertMovie(movieEntity);
                int movieId = movieEntity.getMoNum();

                //region 포스터
                Elements moviePosterLink = movieDoc.select("span.thumb-image > img");
                String moviePoster = moviePosterLink.attr("src");
                if (!moviePoster.isEmpty()) {
                    if(movieId > 0) {
                        this.movieImageMapper.insertMoviePosterUrl(movieId, moviePoster);
                    }
                }
                //endregion
                //region 관람 등급
                String[] parts = movieRawData.split("[,.]\\s*");
                String raiting = parts[0].trim();
                if(!raiting.isEmpty()) {
                    this.raitingMapper.insertMovieRaiting(movieId, raiting);
                }
                //endregion
                //region 장르
                String MovieGenre = movieDoc.select("dt:contains(장르)").text();
                String genre = MovieGenre.replace("장르 :", "").trim();
                GenereEntity genereEntity = new GenereEntity();
                genereEntity.setGeName(genre);
                Integer genum = this.genreMapper.selectGenereIdByName(genre);
                if (genum == null) {
                    this.genreMapper.insertMovieGenre(genereEntity);
                    genum = genereEntity.getGeNum();
                }
                this.genreMapper.insertMovieGenreMapping(movieId, genum);
                //endregion
                //region 제작국가
                for(String part : parts) {
                    String cleanPart = part.trim();
                    if(!cleanPart.isEmpty() && exceptPattern(cleanPart)) {
                        CountryEntity countryEntity = new CountryEntity();
                        countryEntity.setCoName(cleanPart);
                        Integer conum = this.countryMapper.selectCountryIdByName(cleanPart);
                        if(conum == null) {
                            this.countryMapper.insertMovieCountry(countryEntity);
                            conum = countryEntity.getCoNum();
                        }
                        this.countryMapper.insertMovieCountryMapping(movieId, conum);
                    }
                }
                //endregion
                //region 인물
                int questionMarkIndex = href.indexOf('?');
                String NewchUrl = href.substring(questionMarkIndex);
                String chUrl = "http://www.cgv.co.kr/movies/detail-view/cast.aspx" + NewchUrl + "#menu";
                Document CharacterDoc = Jsoup.connect(chUrl).get();
                Elements DirectorLinks = CharacterDoc.select("div.sect-staff-director > ul > li > div.box-image > a");
                Elements CharacterLinks = CharacterDoc.select("div.sect-staff-actor > ul > li > div.box-image > a");
                for (Element DirectorLink : DirectorLinks) {
                    // 감독 자체가 없을 수도 있어서 try catch 사용
                    try {
                        String DirectorHref = DirectorLink.attr("href");
                        DirectorHref = "http://www.cgv.co.kr" + DirectorHref;
                        Document DirectorDocs = Jsoup.connect(DirectorHref).get();
                        //감독 이름 가져오기
                        String DirectorName = DirectorDocs.select("div.box-contents > div.title > strong").text();
                        // 감독 출생 가져오기
                        String DirectorBirthRaw = DirectorDocs.select("dt:contains(출생) + dd").text();
                        LocalDate DirectorBirth = null;
                        if (!DirectorBirthRaw.isEmpty()) {
                            try {
                                DirectorBirth = LocalDate.parse(DirectorBirthRaw); // 유효한 날짜만 변환
                            } catch (Exception e) {
                                DirectorBirth = null;
                            }
                        }
                        // 감독 국적 가져오기
                        String DirectorNation = DirectorDocs.select("dt:contains(국적) + dd").text();
                        if (DirectorNation.isEmpty()) {
                            DirectorNation = null;
                        }
                        CharactorEntity charactorEntity = new CharactorEntity();
                        charactorEntity.setChName(DirectorName);
                        charactorEntity.setChBirth(DirectorBirth);
                        charactorEntity.setChNation(DirectorNation);
                        charactorEntity.setChJob("감독");
                        Integer ChNum = this.charactorMapper.selectCharacterIdByName(DirectorName);
                        if(ChNum == null) {
                            this.charactorMapper.insertCharacter(charactorEntity);
                            ChNum = charactorEntity.getChNum();
                        }
                        //감독 이미지 가져오기
                        Elements DirectorImgLink = DirectorDocs.select("div.sect-base > div.box-image > a > span.thumb-image > img");
                        String DirectorImg = DirectorImgLink.attr("src");
                        if(movieId > 0) {
                            this.charactorMapper.insertCharacterImg(ChNum, DirectorImg);
                        }
                        this.charactorMapper.insertMovieCharacter(movieId, ChNum);

                    } catch (Exception e) {
                        System.out.println("페이지가 존재하지 않습니다.");
                    }
                }
                for (Element CharacterLink : CharacterLinks) {
                    // 배우 자체가 없을 수도 있어서 try catch 사용
                    try {
                        String CharacterHref = CharacterLink.attr("href");
                        CharacterHref = "http://www.cgv.co.kr" + CharacterHref;
                        Document CharacterDocs = Jsoup.connect(CharacterHref).get();
                        //배우 이름 가져오기
                        String CharacterName = CharacterDocs.select("div.box-contents > div.title > strong").text();
                        // 배우 출생 가져오기
                        String CharacterBirthRaw = CharacterDocs.select("dt:contains(출생) + dd").text();
                        LocalDate CharacterBirth = null;
                        if (!CharacterBirthRaw.isEmpty()) {
                            try {
                                CharacterBirth = LocalDate.parse(CharacterBirthRaw); // 유효한 날짜만 변환
                            } catch (Exception e) {
                                CharacterBirth = null;
                            }
                        }
                        // 배우 국적 가져오기
                        String CharacterNation = CharacterDocs.select("dt:contains(국적) + dd").text();
                        if (CharacterNation.isEmpty()) {
                            CharacterNation = null;
                        }
                        CharactorEntity charactorEntity = new CharactorEntity();
                        charactorEntity.setChName(CharacterName);
                        charactorEntity.setChBirth(CharacterBirth);
                        charactorEntity.setChNation(CharacterNation);
                        charactorEntity.setChJob("배우");
                        Integer ChNum = this.charactorMapper.selectCharacterIdByName(CharacterName);
                        if(ChNum == null) {
                            this.charactorMapper.insertCharacter(charactorEntity);
                            ChNum = charactorEntity.getChNum();
                        }
                        //배우 이미지 가져오기
                        Elements CharacterImgLink = CharacterDocs.select("div.sect-base > div.box-image > a > span.thumb-image > img");
                        String CharacterImg = CharacterImgLink.attr("src");
                        if(movieId > 0) {
                            this.charactorMapper.insertCharacterImg(ChNum, CharacterImg);
                        }
                        this.charactorMapper.insertMovieCharacter(movieId, ChNum);
                    } catch (Exception e) {
                        System.out.println("페이지가 존재하지 않습니다.");
                    }
                }
            //endregion
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
