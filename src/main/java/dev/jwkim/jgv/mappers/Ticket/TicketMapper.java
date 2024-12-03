package dev.jwkim.jgv.mappers.Ticket;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper {
    int insertSeat(TicketMapper Seat);

}
