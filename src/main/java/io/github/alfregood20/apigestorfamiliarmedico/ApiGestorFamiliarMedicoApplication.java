package io.github.alfregood20.apigestorfamiliarmedico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ApiGestorFamiliarMedicoApplication {

    public static void main(String[] args) {

        SpringApplication.run(ApiGestorFamiliarMedicoApplication.class, args);
    }
}