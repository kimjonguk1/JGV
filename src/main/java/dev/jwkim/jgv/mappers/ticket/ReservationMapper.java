package dev.jwkim.jgv.mappers.ticket;

import dev.jwkim.jgv.entities.theater.ScreenEntity;
import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import dev.jwkim.jgv.entities.ticket.ReservationEntity;
import dev.jwkim.jgv.entities.ticket.SeatEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface ReservationMapper {

    ScreenEntity[] selectReservationByScNum(@Param("moTitle") String moTitle,
                                            @Param("ciName") String ciName,
                                            @Param("thName") String thName,
                                            @Param("scStartDate") LocalDateTime scStartDate);

    PaymentEntity[] selectPaymentByPaNum(
            @Param("meName") String meName,
            @Param("usNum") int usNum);

    SeatEntity[] selectSeatBySeNum(@Param("seName") String seName,
                                   @Param("ciName") String ciName,
                                   @Param("thName") String thName);

    int insertReservation(ReservationEntity reservation);

    Integer isSeatAlreadyReserved(@Param("seName") String seName,
                                  @Param("ciName") String ciName,
                                  @Param("thName") String thName,
                                  @Param("scStartDate") LocalDateTime scStartDate);
}
