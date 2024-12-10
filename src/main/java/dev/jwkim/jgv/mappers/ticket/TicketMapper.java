package dev.jwkim.jgv.mappers.ticket;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import dev.jwkim.jgv.entities.theater.CinemaEntity;
import dev.jwkim.jgv.entities.theater.CinemaTypeEntity;
import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TicketMapper {
    int insertScreen(ScreenEntity screen);

    MovieEntity selectMovieNumByMovieTitle(@Param(value = "moTitle") String moTitle);

    CinemaEntity selectCinemaNumByCinemaTitle(@Param(value = "ciName") String ciName,
                                              @Param(value = "thName") String thName);

    CinemaEntity selectCinemaNumByCinemaType(@Param(value = "citName") String citName,
                                             @Param(value = "thName") String thName);

    ReservationEntity[] selectSeatByReservationSeNum(
            @Param("ciName") String ciName,
            @Param("thName") String thName);

    SeatEntity[] selectSeatBySeName(
            @Param("ciName") String ciName,
            @Param("thName") String thName);

    CinemaTypeEntity[] selectSeatByCitPrice(
            @Param("ciName") String ciName,
            @Param("thName") String thName);
}
