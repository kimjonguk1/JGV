package dev.jwkim.jgv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class JgvApplication {

    public static void main(String[] args) {
        SpringApplication.run(JgvApplication.class, args);
    }

}
