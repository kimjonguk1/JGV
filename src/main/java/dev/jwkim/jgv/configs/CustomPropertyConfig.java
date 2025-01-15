package dev.jwkim.jgv.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "custom.property")
@Component
@Getter
@Setter
public class CustomPropertyConfig {
    private String kakaoClientId;
    private String kakaoRedirectUri;
    private String kakaoLogoutRedirectUri;
    private String naverClientId;
    private String naverClientSecret;
    private String naverRedirectUri;
}
