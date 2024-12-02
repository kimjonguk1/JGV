package dev.jwkim.jgv.DTO;

import dev.jwkim.jgv.entities.Movie.MovieEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Movie_ImageDTO extends MovieEntity {
    private int moNum;
    private String moTitle;
    private LocalDate moDate;
    private int moTime;
    private String moPlot;
    private Float moBookingRate;
    private LocalDate moEnding;
    private String m_img_url;
}
