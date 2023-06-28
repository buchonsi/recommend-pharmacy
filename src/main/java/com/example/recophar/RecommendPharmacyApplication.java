package com.example.recophar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RecommendPharmacyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendPharmacyApplication.class, args);
    }

}
