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
    private List<String> countries;
    private List<String> actorImages;

    // 수정: actors를 MovieCharacterDTO 리스트로 변경
    private List<MovieCharacterDTO> actors;

    private List<MovieCharacterDTO> movieCharacters;

    @Getter
    @Setter
    public static class MovieCharacterDTO {
        private int chNum;
        private String chName;
        private boolean delete;
    }

    @Override
    public String toString() {
        return "AllMovieInfoDTO{" +
                "moNum=" + moNum +
                ", raNum=" + raNum +
                ", geNum=" + geNum +
                ", mImgNum=" + mImgNum +
                ", chNum=" + chNum +
                ", cImgNum=" + cImgNum +
                ", coNum=" + coNum +
                ", moTitle='" + moTitle + '\'' +
                ", moDate=" + moDate +
                ", moTile=" + moTile +
                ", moPlot='" + moPlot + '\'' +
                ", moBookingRate=" + moBookingRate +
                ", moEnding=" + moEnding +
                ", raGrade='" + raGrade + '\'' +
                ", geName='" + geName + '\'' +
                ", mImgUrl='" + mImgUrl + '\'' +
                ", chName='" + chName + '\'' +
                ", chJob='" + chJob + '\'' +
                ", chBirth=" + chBirth +
                ", chNation='" + chNation + '\'' +
                ", cImgUrl='" + cImgUrl + '\'' +
                ", coName='" + coName + '\'' +
                ", genres=" + genres +
                ", actors=" + actors +
                ", countries=" + countries +
                ", actorImages=" + actorImages +
                '}';
    }
}
