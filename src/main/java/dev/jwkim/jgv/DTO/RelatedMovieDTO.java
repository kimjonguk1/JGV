package dev.jwkim.jgv.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatedMovieDTO {
    private Integer moNum;
    private String movieImage;
    private String movieTitle;

    public RelatedMovieDTO(String movieTitle, String movieImage, Integer moNum) {
        this.movieTitle = movieTitle;
        this.movieImage = movieImage;
        this.moNum = moNum;
    }
}
