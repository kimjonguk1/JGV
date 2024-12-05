package dev.jwkim.jgv.entities.movie;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"coNum"})
public class CountryEntity {
    private int coNum;
    private String coName;
}
