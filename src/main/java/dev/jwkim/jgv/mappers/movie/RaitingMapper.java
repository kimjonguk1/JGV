package dev.jwkim.jgv.mappers.movie;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RaitingMapper {
    int insertMovieRaiting(@Param("moNum") int moNum, @Param("ra_grade") String ra_grade);
    int deleteAllMovieRaiting();
}
