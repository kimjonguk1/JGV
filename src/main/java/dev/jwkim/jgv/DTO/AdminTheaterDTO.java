package dev.jwkim.jgv.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class AdminTheaterDTO {
    private int scNum;
    private String mImgUrl;
    private String thName;
    private String ciName;
    private String moTitle;
    private LocalDateTime scStartDate;
}
