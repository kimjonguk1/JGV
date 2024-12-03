package dev.jwkim.jgv.entities.Ticket;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode(of = {"paNum"})
public class PaymentEntity {
private int paNum;
private int paPrice;
private boolean paState;
private LocalDateTime paCreatedAt;
private LocalDateTime paDeletedAt;
private int usNum;
private int meNum;
}
