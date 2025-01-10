package dev.jwkim.jgv.entities.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"ipIndex"})
public class UserBlockedIpsEntity {
    private int ipIndex;
    private String ipClientIp;
    private LocalDateTime ipCreatedAt;
    private boolean ipExpiresAt;
}
