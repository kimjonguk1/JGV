package dev.jwkim.jgv.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Movie_InfoDTO extends Movie_ImageDTO{
    private String grade;
    private String genres;
    private String directorName;
    private String directorImage;
    private List<String> actorNames;
    private List<String> actorImages;
    private String countries;
    private List<Map<String, Object>> relatedMovies;
}
