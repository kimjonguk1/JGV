package dev.jwkim.jgv.entities.theater;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"regNum"})
public class RegionEntity {
    private int regNum;
    private String regName;
}
