package dev.jwkim.jgv.entities.Movie;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"geNum"})
public class GenereEntity {
    private int geNum;
    private String geName;
}
