package dev.jwkim.jgv.mappers.ticket;

import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TicketMapper {
    ReservationEntity[] selectSeatByReservationSeNum(
            @Param("ciName") String ciName,
            @Param("thName") String thName);

    SeatEntity[] selectSeatBySeName(
            @Param("ciName") String ciName,
            @Param("thName") String thName);
}
