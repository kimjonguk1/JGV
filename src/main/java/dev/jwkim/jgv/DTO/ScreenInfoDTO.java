package dev.jwkim.jgv.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ScreenInfoDTO {
    private int scNum;
    private String ciName;
    private Date scStartDate;
    private String mImgUrl;
}
