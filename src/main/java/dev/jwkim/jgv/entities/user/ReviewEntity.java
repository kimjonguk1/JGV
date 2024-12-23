package dev.jwkim.jgv.entities.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"reNum"})
public class ReviewEntity {
    private int reNum;
    private String reContent;
    private LocalDateTime reCreatedAt;
    private LocalDateTime reUpdatedAt;
    private LocalDateTime reDeletedAt;
    private int reLiked;
    private int moNum;
    private int usNum;
}
