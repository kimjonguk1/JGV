package dev.jwkim.jgv.mappers.Ticket;

import dev.jwkim.jgv.entities.Ticket.SeatEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TicketMapper {
    SeatEntity[] selectSeatByCiName(@Param("ciName") String ciName,
                                    @Param("thName") String thName);

}
