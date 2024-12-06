package dev.jwkim.jgv.mappers.Movie;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import dev.jwkim.jgv.entities.Movie.RatingEntity;

@Mapper
public interface RaitingMapper {
    int insertMovieRaiting(RatingEntity ratingEntity);
    int deleteAllMovieRaiting();
    Integer selectRaitingIdByName(String RaitingName);
}
