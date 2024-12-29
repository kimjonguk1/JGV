package dev.jwkim.jgv.mappers.admin;

import dev.jwkim.jgv.DTO.AdminMovieDTO;
import dev.jwkim.jgv.DTO.AdminTheaterDTO;
import dev.jwkim.jgv.entities.movie.MovieEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {

    // 영화
    MovieEntity[] selectAllMovie();

    int selectArticleCountByMovieName();

    int searchArticleCountByMovieName(@Param("filter") String filter, @Param("keyword") String keyword);

    AdminMovieDTO[] selectAllDTO();

    AdminMovieDTO[] selectArticleByMovieName(
                                        @Param("limitCount") int limitCount,
                                        @Param("offsetCount") int offsetCount);

    AdminMovieDTO[] searchArticleByMovieName(
                                        @Param("limitCount") int limitCount,
                                        @Param("offsetCount") int offsetCount,
                                        @Param("filter") String filter,
                                        @Param("keyword") String keyword);


    // 상영관
    int selectArticleCountByTheater();

    AdminTheaterDTO[] selectAllDTOByTheater(
            @Param("limitCount") int limitCount,
            @Param("offsetCount") int offsetCount
    );
}
