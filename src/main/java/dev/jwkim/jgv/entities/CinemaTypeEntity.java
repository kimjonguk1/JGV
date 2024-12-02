package dev.jwkim.jgv.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"citNum"})
public class CinemaTypeEntity {
    private int citNum;
    private String citName;
    private int citPrice;
}
