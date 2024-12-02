package dev.jwkim.jgv.entities.Movie;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode(of = {"chNum"})
public class CharactorEntity {
    private int chNum;
    private String chName;
    private String chJob;
    private LocalDate chBirth;
    private String chNation;
}
