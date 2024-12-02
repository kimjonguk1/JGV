package dev.jwkim.jgv.entities.Movie;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"raNum"})
public class RatingEntity {
    private int raNum;
    private String raGrade;
    private int MoNum;
}
