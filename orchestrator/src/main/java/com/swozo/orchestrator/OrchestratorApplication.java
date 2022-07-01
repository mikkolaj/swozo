package com.swozo.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@RequiredArgsConstructor
@ConfigurationPropertiesScan("com.swozo.orchestrator")
public class OrchestratorApplication {
    private final Playground playground;

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApplication.class, args);
    }

    // Local testing
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        System.out.println("Hello world, I have just started up");
        playground.run();
    }

    @PostConstruct
    public void init() {
        // we should probably use -Dspring-boot.run.jvmArguments="-Duser.timezone=UTC"
        // https://stackoverflow.com/questions/54316667/how-do-i-force-a-spring-boot-jvm-into-utc-time-zone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
