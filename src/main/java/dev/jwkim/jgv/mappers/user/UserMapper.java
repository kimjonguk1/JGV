package dev.jwkim.jgv.mappers.user;

import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import dev.jwkim.jgv.entities.user.UserBlockedIpsEntity;
import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.entities.user.UserLoginAttemptsEntity;
import dev.jwkim.jgv.results.CommonResult;
import dev.jwkim.jgv.vos.user.ReservationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface UserMapper {
    int updateSocial(@Param("usSocialId") String socialId,
                     @Param("usSocialTypeCode") String usSocialTypeCode,
                     @Param("usEmail") String usEmail);

    UserEntity selectUserBySocialTypeCodeAndSocialId(@Param("usSocialTypeCode") String socialTypeCode,
                                                     @Param("usSocialId") String socialId);

    UserEntity selectUserByEmailAndSocialType(@Param("usEmail") String email,
                                              @Param("usSocialTypeCode") String socialTypecode);


    int insertUser(UserEntity user);

    int insertAttempts(UserLoginAttemptsEntity attempts);

    int insertBlockedIp(UserBlockedIpsEntity blockedIp);

    int countFailedLoginAttempts(@Param("clientIp") String clientIp);

    int selectPageByUserId(@Param("userId") String userId);

    int searchPageByLoginHistory(@Param("userId") String userId,
                                 @Param("startDate") String startDate,
                                 @Param("endDate") String endDate);

    UserLoginAttemptsEntity[] searchLoginAttemptsByUserId(@Param("userId") String userId,
                                                          @Param("limitCount") int limitCount,
                                                          @Param("offsetCount") int offsetCount,
                                                          @Param("startDate") String startDate,
                                                          @Param("endDate") String endDate);

    UserLoginAttemptsEntity[] selectLoginAttemptsByUserId(@Param("userId") String userId,
                                                          @Param("limitCount") int limitCount,
                                                          @Param("offsetCount") int offsetCount);

    UserBlockedIpsEntity selectBlockedIpByClientIp(@Param("clientIp") String clientIp);

    int findAllReservations(@Param("usNum") int usNum);

    UserEntity selectUserById(@Param("id") String id);

    UserEntity selectUserByEmail(@Param("usEmail") String email);

    UserEntity selectUserByContact(@Param("usContact") String contact);

    UserEntity selectUserByNickname(@Param("usNickname") String nickname);

    UserEntity FindUserByEmail(@Param("usName") String name,
                               @Param("usEmail") String email,
                               @Param("usContact") String contact);

    UserEntity FindUserById(@Param("usId") String id,
                            @Param("usEmail") String email,
                            @Param("usContact") String contact);

    int updateUser(UserEntity user);

    UserEntity selectUserByUserId(@Param("id") String id);


    // 이메일 미인증 계정 삭제
    List<UserEntity> selectUnverifiedUsersWithExpiredToken(@Param("now") LocalDateTime now);

    int deleteUserById(@Param("usId") String id);

    ReservationVo[] selectPaymentByUsNum(@Param("usNum") int usNum, @Param("limitCount") int limitCount, @Param("offsetCount") int offsetCount);
    ReservationVo[] selectCancelPaymentByUsNum(@Param("usNum") int usNum);
    PaymentEntity[] selectCancelPaNumByPayment(@Param("paNum") int paNum,
                                               @Param("usNum") int usNum,
                                               @Param("paPrice") int paPrice,
                                               @Param("paCreatedAt") String paCreatedAt);

    int updatePaymentState(@Param("usNum") int usNum,
                           @Param("paNum") int paNum,
                           @Param("paState") boolean paState,
                           @Param("paDeletedAt") LocalDateTime paDeletedAt);

    int selectArticleByUsNumCount(@Param("usNum") int usNum);


    int deleteReviewsByUserId(int usNum);
    ReservationVo selectCancelPaNumByAll(@Param("usNum") int usNum, @Param("paNum") int paNum);

    int getReserveInfoById(@Param("usId") int usId, @Param("moNum") int moNum);
}
