package dev.jwkim.jgv.vos.theater;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScreenDataVo {
    public final int seatCount;
    public final int usedSeatCount;
    public final int emptySeatCount;

    public ScreenDataVo(int seatCount, int usedSeatCount) {
        this.seatCount = seatCount;
        this.usedSeatCount = usedSeatCount;
        this.emptySeatCount = seatCount - usedSeatCount;
    }

}
