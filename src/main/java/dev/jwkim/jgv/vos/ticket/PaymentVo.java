package dev.jwkim.jgv.vos.ticket;

import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaymentVo extends PaymentEntity {
    private int scNum;
}
