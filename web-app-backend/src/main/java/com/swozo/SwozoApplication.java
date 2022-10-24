package com.swozo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.swozo")
public class SwozoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwozoApplication.class, args);
    }
}
