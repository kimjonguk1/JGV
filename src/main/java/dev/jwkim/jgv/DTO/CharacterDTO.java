package dev.jwkim.jgv.DTO;

import dev.jwkim.jgv.entities.movie.CharactorEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterDTO extends CharactorEntity {
    private Integer CImgNum;
    private String CImgUrl;
}
