package dev.jwkim.jgv.entities.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@EqualsAndHashCode
public class UserEntity {
    private int usNum;
    private String usId;
    private String usPw;
    private String usName;
    private String usNickName;
    private Date usBirth;
    private String usGender;
    private String usEmail;
    private String usContact;
    private String usAddr;
    private LocalDateTime usCreatedAt;
    private LocalDateTime usUpdatedAt;
    private Boolean usIsDeleted;
    private Boolean usIsAdmin;
    private Boolean usIsSuspended;
    private Boolean usIsVerified;
}
