package dev.jwkim.jgv.entities.movie;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"CImageNum"})
public class CharactorImageEntity {
    private int CImageNum;
    private String CImageUrl;
    private int chNum;
}
