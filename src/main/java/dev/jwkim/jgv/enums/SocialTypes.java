package dev.jwkim.jgv.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum SocialTypes implements DefaultEnum {
    KAKAO("KAKAO", "카카오"),
    NAVER("NAVER", "네이버");

    private final String code;
    private final String text;

    SocialTypes(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public static Optional<SocialTypes> parse(String socialTypeCode) {
        return Arrays.stream(SocialTypes.values()).filter((x) -> x.getCode().equals(socialTypeCode)).findFirst();
    }
}