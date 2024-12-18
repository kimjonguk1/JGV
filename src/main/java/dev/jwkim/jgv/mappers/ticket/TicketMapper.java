package dev.jwkim.jgv.mappers.ticket;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.entities.theater.CinemaEntity;
import dev.jwkim.jgv.entities.theater.CinemaTypeEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.theater.TheaterEntity;
import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import dev.jwkim.jgv.vos.theater.MovieVo;
import dev.jwkim.jgv.vos.theater.RegionVo;
import dev.jwkim.jgv.vos.theater.ScreenVo;
import dev.jwkim.jgv.vos.ticket.CinemaTypeVo;
import dev.jwkim.jgv.vos.ticket.SeatVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface TicketMapper {
    int insertScreen(ScreenEntity screen);

    TheaterEntity selectTheater(@Param("thName") String thName);

    ScreenVo[] selectScreenDatesByMovieAndTheaterAndDate(@Param("movie") int moNum,
                                                         @Param("theater") int thNum,
                                                         @Param("date") String scStartDate);

    ScreenEntity[] selectAllScreenDates();

    RegionVo[] selectRegionAndTheaterCount();

    MovieVo[] selectAllMoviesByRating();

    MovieVo[] selectAllMoviesByKorea();

    MovieVo[] selectAllMoviesByMoTitle(@Param(value = "moTitle") String moTitle);

    MovieEntity selectMovieNumByMovieTitle(@Param(value = "moTitle") String moTitle);

    CinemaEntity selectCinemaNumByCinemaTitle(@Param(value = "ciName") String ciName,
                                              @Param(value = "thName") String thName);

    CinemaEntity selectCinemaNumByCinemaType(@Param(value = "citName") String citName,
                                             @Param(value = "thName") String thName);


    ScreenEntity[] selectDuplicateScreen(@Param(value = "startDate") LocalDateTime startDate,
                                         @Param(value = "moNum") int moNum,
                                         @Param(value = "ciNum") int ciNum);

//    ---------------------------------------

    SeatVo[] selectSeatByReservationSeNum(
            @Param("ciName") String ciName,
            @Param("thName") String thName,
            @Param("moTitle") String moTitle,
            @Param("scStartDate") LocalDateTime scStartDate);

    CinemaTypeVo[] selectSeatByCitPrice(
            @Param("ciName") String ciName,
            @Param("thName") String thName,
            @Param("moTitle") String moTitle,
            @Param("scStartDate") LocalDateTime scStartDate);
}
