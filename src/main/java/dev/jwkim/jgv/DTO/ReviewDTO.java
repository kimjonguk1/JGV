package dev.jwkim.jgv.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDTO {
    private int reNum;
    private String reContent;
    private LocalDateTime reCreatedAt;
    private Long reCreatedAtTimestamp;
    private LocalDateTime reUpdatedAt;
    private LocalDateTime reDeletedAt;
    private int reLiked;
    private int moNum;
    private int usNum;
    private String usNickName;
}
