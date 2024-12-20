package dev.jwkim.jgv.vos.ticket;


import dev.jwkim.jgv.entities.theater.CinemaTypeEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaTypeVo extends CinemaTypeEntity {
    private int moTime;
    private String mImgUrl;

}
