package dev.jwkim.jgv.services.ticket;

import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import dev.jwkim.jgv.mappers.ticket.TicketMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketMapper ticketMapper;

    public ReservationEntity[] selectSeatByReservationNum(String ciName, String thName) {

        ReservationEntity[] seatNum = this.ticketMapper.selectSeatByReservationSeNum(ciName, thName);


        return seatNum;
    }

    public SeatEntity[] selectSeatBySeatName(String ciName, String thName) {

        SeatEntity[] seatName = this.ticketMapper.selectSeatBySeName(ciName, thName);
        return seatName;
    }
}
