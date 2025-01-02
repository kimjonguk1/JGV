package dev.jwkim.jgv.vos.user;

import dev.jwkim.jgv.entities.ticket.PaymentEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationVo extends PaymentEntity {
    private String thName;         // 극장 이름
    private String ciName;         // 영화관 이름
    private LocalDateTime scStartDate;    // 상영 시작 날짜
    private String seName;         // 좌석 이름
    private String mImgUrl;        // 영화 이미지 URL
    private String moTitle;     // 영화 제목
    private String meName;
    private String moNum;
    private String seNames;


    public String getSeNames() {
        return seNames;
    }

    public void setSeNames(String seNames) {
        this.seNames = seNames;
    }
}
