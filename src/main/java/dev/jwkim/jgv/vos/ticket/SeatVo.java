package dev.jwkim.jgv.vos.ticket;


import dev.jwkim.jgv.entities.ticket.SeatEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatVo extends SeatEntity {
    private String scNum;
}
