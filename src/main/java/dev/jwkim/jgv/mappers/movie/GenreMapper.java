package dev.jwkim.jgv.mappers.movie;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GenreMapper {
    int insertMovieGenre(@Param("geName") String geName);
    int deleteAllMovieGenre();
}
