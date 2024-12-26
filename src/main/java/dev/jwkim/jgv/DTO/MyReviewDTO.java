package dev.jwkim.jgv.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MyReviewDTO {
    private int reNum;
    private int moNum;
    private int usNum;
    private String reContent;
    private int reLiked;
    private String usNickName;
    private String moTitle;
    private LocalDateTime reCreatedAt;
    private int mImgNum;
    private String mImgUrl;
}
