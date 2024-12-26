package dev.jwkim.jgv.mappers.user;

import dev.jwkim.jgv.entities.user.UserEntity;
import dev.jwkim.jgv.vos.user.ReservationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface UserMapper {

    int insertUser(UserEntity user);

    UserEntity selectUserById(@Param("usId") String id);

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

    // 이메일 미인증 계정 삭제
    List<UserEntity> selectUnverifiedUsersWithExpiredToken(@Param("now") LocalDateTime now);

    int deleteUserById(@Param("usId") String id);

    ReservationVo[] selectPaymentByUsNum(@Param("usNum") int usNum);
    ReservationVo[] selectCancelPaymentByUsNum(@Param("usNum") int usNum);

}
