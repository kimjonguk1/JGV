package dev.jwkim.jgv.mappers.movie;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MovieImageMapper {
    int insertMoviePosterUrl(@Param("moNum") int moNum, @Param("imgUrl") String imgUrl);
    int updateMoviePosterUrl(@Param("moNum") int moNum, @Param("imgUrl") String imgUrl);
    String selectPosterUrlByMovieId(@Param("moNum") int moNum);
}
