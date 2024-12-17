package dev.jwkim.jgv.vos.theater;

import dev.jwkim.jgv.entities.theater.ScreenEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScreenVo extends ScreenEntity {
    private String ciName;
    private int thNum;
    private int citNum;
    private String citName;
    private int seatCount;
}
