package dev.jwkim.jgv.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AllMovieInfoDTO {
    private int moNum;
    private int raNum;
    private int geNum;
    private int mImgNum;
    private int chNum;
    private int cImgNum;
    private int coNum;

    private String moTitle;
    private LocalDateTime moDate;
    private int moTile;
    private String moPlot;
    private Float moBookingRate;
    private LocalDateTime moEnding;
    private String raGrade;
    private String geName;
    private String mImgUrl;
    private String chName;
    private String chJob;
    private LocalDateTime chBirth;
    private String chNation;
    private String cImgUrl;
    private String coName;

    private List<String> genres;
    private List<String> actors;
    private List<String> countries;
    private List<String> actorImages;
}
