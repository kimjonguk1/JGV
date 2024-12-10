package dev.jwkim.jgv.entities.movie;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(of = {"moNum"})
public class MovieEntity {
    private int moNum;
    private String moTitle;
    private String moDate;
    private int moTime;
    private String moPlot;
    private Float moBookingRate;
    private LocalDate moEnding;
    private int raNum;
}
