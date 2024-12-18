package dev.jwkim.jgv.mappers.movie;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import dev.jwkim.jgv.entities.movie.RatingEntity;

@Mapper
public interface RaitingMapper {
    int insertMovieRaiting(RatingEntity ratingEntity);
    Integer selectRaitingIdByName(String RaitingName);
}
