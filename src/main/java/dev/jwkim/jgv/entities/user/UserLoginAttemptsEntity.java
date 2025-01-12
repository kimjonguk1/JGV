package dev.jwkim.jgv.entities.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class UserLoginAttemptsEntity {
    private int atIndex;
    private String atUserId;
    private String atClientIp;
    private String atClientUa;
    private boolean atResult;
    private LocalDateTime atCreatedAt;

}
