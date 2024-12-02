package dev.jwkim.jgv.mappers.Movie;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GenreMapper {
    int insertMovieGenre(@Param("geName") String geName);
    int deleteAllMovieGenre();
}
