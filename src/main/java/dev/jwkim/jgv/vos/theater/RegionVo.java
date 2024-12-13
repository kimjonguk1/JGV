package dev.jwkim.jgv.vos.theater;

import dev.jwkim.jgv.entities.theater.RegionEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionVo extends RegionEntity {
    private int theaterCount;
}
