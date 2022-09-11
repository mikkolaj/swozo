package com.swozo.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
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

}
