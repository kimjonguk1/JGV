package dev.jwkim.jgv.controlles.movie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jwkim.jgv.DTO.CharacterDTO;
import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.DTO.Movie_InfoDTO;
import dev.jwkim.jgv.DTO.RelatedMovieDTO;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.services.movie.MovieService;
import dev.jwkim.jgv.services.movie.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/movies")
public class MoiveListController {
    private final MovieService movieService;
    private final SearchService searchService;

    public MoiveListController(MovieService movieService, SearchService searchService) {
        this.movieService = movieService;
        this.searchService = searchService;
    }

    @RequestMapping(value = "/movieList", method = RequestMethod.GET)
    public ModelAndView getMovieList() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("article/MovieList");
        List<Movie_ImageDTO> movies = this.movieService.selectAllMovieList();

        // 데이터 변환 및 분류
        List<Movie_ImageDTO> nowPlaying = new ArrayList<>();
        List<Movie_ImageDTO> upcomingMovies = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        //현재 날짜 기준으로 이후 개봉하는 영화는 상영예정작
        //개봉일이 잘못된 형식의 데이터이거나 현재날짜 기준 이전에 개봉한 영화이면 무비차트
        for (Movie_ImageDTO movie : movies) {
            try {
                LocalDate releaseDate = LocalDate.parse(movie.getMoDate(), formatter);
                if (!releaseDate.isAfter(today)) {
                    nowPlaying.add(movie);
                } else {
                    upcomingMovies.add(movie);
                }
            } catch (DateTimeParseException e) {
                upcomingMovies.add(movie);
            }
        }
        mav.addObject("nowPlaying", nowPlaying);
        mav.addObject("upcomingMovies", upcomingMovies);
        return mav;
    }

    @RequestMapping(value = "/movieList/movieInfo/{id}")
    public ModelAndView getMovieDetail(@PathVariable("id") Integer id) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("article/MovieInfo");
        Movie_InfoDTO movieInfo = movieService.selectMovieInfoById(id);
        mav.addObject("movieInfo", movieInfo);
        return mav;
    }

    @RequestMapping(value = "/movieList/person/{id}")
    public String getPersonDetail(@PathVariable("id") Integer id, Model model) {
        return null;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ModelAndView search(@RequestParam String keyword) {
        ModelAndView mav = new ModelAndView();
        //영화 검색
        List<Movie_ImageDTO> movies = searchService.searchMoviesByKeyword(keyword);
        //인물 검색
        List<CharacterDTO> people = searchService.searchPeopleByKeyword(keyword);
        // 인물별 관련 영화 검색 (감독 또는 배우)
        Map<String, String> relatedMoviesJsonMap = new HashMap<>(); // JSON 문자열을 담을 Map
        ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper

        for (CharacterDTO person : people) {
            List<RelatedMovieDTO> relatedMovies = searchService.searchMoviesByActor(person.getChName())
                    .stream()
                    .map(movieMap -> {
                        String title = (String) movieMap.get("movieTitle");
                        String image = (String) movieMap.get("movieImage");
                        Number moNum = (Number) movieMap.get("moNum");
                        Integer id = moNum.intValue();
                        return new RelatedMovieDTO(title, image, id);
                    }).collect(Collectors.toList());

            try {
                // 관련 영화 리스트를 JSON 문자열로 변환
                String jsonString = objectMapper.writeValueAsString(relatedMovies);
                relatedMoviesJsonMap.put(person.getChName(), jsonString);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                // 변환 실패 시 빈 문자열로 처리
                relatedMoviesJsonMap.put(person.getChName(), "[]");
            }
        }

        if(movies == null) {
            movies = new ArrayList<>();
        }

        if(people == null) {
            people = new ArrayList<>();
        }
        mav.addObject("people", people);
        mav.addObject("movies", movies);
        mav.addObject("relatedMoviesJsonMap", relatedMoviesJsonMap);

        mav.setViewName("article/MovieSearch");
        return mav;
    }
}
