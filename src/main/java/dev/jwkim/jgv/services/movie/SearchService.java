package dev.jwkim.jgv.services.movie;

import dev.jwkim.jgv.DTO.CharacterDTO;
import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.DTO.Movie_InfoDTO;
import dev.jwkim.jgv.mappers.movie.MovieMapper;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.vos.PageVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    private final MovieMapper movieMapper;

    public SearchService(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public Pair<PageVo, List<Movie_ImageDTO>> searchMoviesByKeyword (String keyword, int requestPage) {

        int totalCount = movieMapper.getMovieCountByKeyword(keyword);

        PageVo pageVo = new PageVo(requestPage, totalCount);

        List<Movie_ImageDTO> movies = movieMapper.findMovieByKeyword(keyword, pageVo.offsetCount, pageVo.countPerPage);

        return Pair.of(pageVo, movies);
    }

    public List<CharacterDTO> searchPeopleByKeyword (String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }
        return this.movieMapper.findCharacterByKeyword(keyword);
    }

    public List<Movie_ImageDTO> searchMoviesByPersonKeyword(String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }
        return this.movieMapper.searchMoviesByPersonKeyword(keyword);
    }

    public List<Map<String, Object>> searchMoviesByActor(String actorName) {
        return this.movieMapper.findMoviesByActorName(actorName);
    }

}
