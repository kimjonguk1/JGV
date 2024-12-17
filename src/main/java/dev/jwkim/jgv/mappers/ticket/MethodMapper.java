package dev.jwkim.jgv.mappers.ticket;

import dev.jwkim.jgv.entities.ticket.MethodEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MethodMapper {

    MethodEntity selectPaymentMeNum(@Param("meName") String meName);
}
