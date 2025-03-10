package com.boilerplate.boilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableJpaAuditing
@EnableMethodSecurity(prePostEnabled = true)
@SpringBootApplication
public class BoilerplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoilerplateApplication.class, args);
    }

}
