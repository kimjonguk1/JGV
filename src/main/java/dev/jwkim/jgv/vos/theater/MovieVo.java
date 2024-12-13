package dev.jwkim.jgv.vos.theater;

import dev.jwkim.jgv.entities.movie.MovieEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieVo extends MovieEntity {
    private String raGrade;
}
