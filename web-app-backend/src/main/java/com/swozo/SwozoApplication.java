package com.swozo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class SwozoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwozoApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // we should probably use -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"
        // https://stackoverflow.com/questions/54316667/how-do-i-force-a-spring-boot-jvm-into-utc-time-zone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
