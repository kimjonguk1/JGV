package dev.jwkim.jgv.entities.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"emEmail", "emKey"})
public class EmailTokenEntity {
    private String emEmail;
    private String emKey;
    private LocalDateTime emCreatedAt;
    private LocalDateTime emExpiresAt;
    private boolean emUsed;
}
