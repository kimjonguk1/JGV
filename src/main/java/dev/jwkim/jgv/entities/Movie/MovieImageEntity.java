package dev.jwkim.jgv.entities.Movie;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"mImgNum"})
public class MovieImageEntity {
    private int mImgNum;
    private String mImgUrl;
    private int MoNum;
}
