package dev.jwkim.jgv.services.Ticket;

import dev.jwkim.jgv.entities.Ticket.SeatEntity;
import dev.jwkim.jgv.mappers.Ticket.TicketMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class  TicketService {
    private final TicketMapper ticketMapper;
    public SeatEntity[] selectSeatByCiName(String ciName, String thName){
        SeatEntity[] seats = this.ticketMapper.selectSeatByCiName(ciName, thName);
        return seats;
    }
}
