package dev.jwkim.jgv.mappers.user;

import dev.jwkim.jgv.entities.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface UserMapper {

    int insertUser(UserEntity user);

    UserEntity selectUserById(@Param("usId") String id);

    UserEntity selectUserByEmail(@Param("usEmail") String email);

    UserEntity selectUserByContact(@Param("usContact") String contact);

    UserEntity selectUserByNickname(@Param("usNickname") String nickname);

    int updateUser(UserEntity user);
}
