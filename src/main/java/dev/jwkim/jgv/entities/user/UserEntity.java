package dev.jwkim.jgv.entities.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@EqualsAndHashCode(of = {"usNum"})
public class UserEntity {
    private int usNum;
    private String usId;
    private String usPw;
    private String usName;
    private String usNickName;
    private LocalDate usBirth;
    private String usGender;
    private String usEmail;
    private String usContact;
    private String usAddr;
    private LocalDateTime usCreatedAt;
    private LocalDateTime usUpdatedAt;
    // private boolean usIsDeleted; // true , false
    private boolean usIsDeleted;    // true , false, null
    private boolean usIsAdmin;
    private boolean usIsSuspended;
    private boolean usIsVerified;

}
