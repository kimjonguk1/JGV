package dev.jwkim.jgv.entities.ticket;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(of = {"meNum"})
public class MethodEntity {
private int meNum;
private String meName;
}
