package dev.jwkim.jgv.mappers.admin;

import dev.jwkim.jgv.DTO.AdminMovieDTO;
import dev.jwkim.jgv.DTO.AdminTheaterDTO;
import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

@Mapper
public interface AdminMapper {

    // 영화
    MovieEntity[] selectAllMovie();

    UserEntity selectUserByIndex(@Param("index") int usNum);

    int totalUsers();

    int searchUserByKeyword(@Param("filter") String filter,
                            @Param("keyword") String keyword);

    UserEntity[] selectUserByKeyword( @Param("limitCount") int limitCount,
                                      @Param("offsetCount") int offsetCount,
                                      @Param("filter") String filter,
                                      @Param("keyword") String keyword);


    UserEntity[] selectAllUsers(@Param("limitCount") int limitCount,
                                @Param("offsetCount") int offsetCount);

    int updateUserBySuspend(UserEntity user);

    int selectArticleCountByMovieName();

    int searchArticleCountByMovieName(@Param("filter") String filter,
                                      @Param("keyword") String keyword);

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
    int updateTheater(ScreenEntity screen);

    int selectArticleCountByTheater();

    int searchArticleCountByTheater(@Param("screenFilter") String screenFilter,
                                    @Param("screenKeyword") String screenKeyword);

    AdminTheaterDTO[] selectAllDTOByTheaters(@Param("limitCount") int limitCount,
                                             @Param("offsetCount") int offsetCount);

    AdminTheaterDTO[] searchAllDTOByTheaters(@Param("limitCount") int limitCount,
                                             @Param("offsetCount") int offsetCount,
                                             @Param("screenFilter") String screenFilter,
                                             @Param("screenKeyword") String screenKeyword);

    ScreenEntity selectScreenByScNum(@Param("scNum") int scNum);
}
