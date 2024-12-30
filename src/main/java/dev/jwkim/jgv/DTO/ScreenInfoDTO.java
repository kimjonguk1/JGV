package dev.jwkim.jgv.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class ScreenInfoDTO {
    private int scNum;
    private String ciName;
    private LocalDateTime scStartDate;
    private String mImgUrl;
}
