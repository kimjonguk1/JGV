package dev.jwkim.jgv.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"ciNum"})
public class CinemaEntity {
    private int ciNum;
    private String ciName;
    private int thNum;
    private int citNum;
}
