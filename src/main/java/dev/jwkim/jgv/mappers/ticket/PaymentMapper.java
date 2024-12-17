package dev.jwkim.jgv.mappers.ticket;

import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    int insertPayment(PaymentEntity payment);
}
