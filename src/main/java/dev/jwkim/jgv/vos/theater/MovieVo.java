package dev.jwkim.jgv.vos.theater;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MovieVo extends MovieEntity {
    private String raGrade;
    private String mImgUrl;
    private LocalDateTime scStartDate;
    private String thName;
    private int theaterCount;
    private String regName;
    private String geName;
    private String citName;
}
