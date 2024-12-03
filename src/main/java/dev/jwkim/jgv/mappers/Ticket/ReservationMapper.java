package dev.jwkim.jgv.mappers.Ticket;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationMapper {
    int insertReservation (ReservationMapper reservation);
}
