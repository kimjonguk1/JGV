package dev.jwkim.jgv.services.movie;

import dev.jwkim.jgv.DTO.CharacterDTO;
import dev.jwkim.jgv.DTO.Movie_ImageDTO;
import dev.jwkim.jgv.DTO.Movie_InfoDTO;
import dev.jwkim.jgv.mappers.movie.MovieMapper;
import dev.jwkim.jgv.results.CommonResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    private final MovieMapper movieMapper;

    public SearchService(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    public List<Movie_ImageDTO> searchMoviesByKeyword (String keyword) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }
        return this.movieMapper.findMovieByKeyword(keyword);
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
}
