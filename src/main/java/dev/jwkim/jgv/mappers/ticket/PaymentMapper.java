package dev.jwkim.jgv.mappers.ticket;

import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {
    int insertPayment(PaymentEntity payment);

    int updatePaymentState(@Param("paNum") int paNum,
                           @Param("paState") boolean paState);

    int deletePayment(@Param("paNum") int paNum);


}
