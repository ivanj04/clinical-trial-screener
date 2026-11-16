package com.ivanjiang.ctscreener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class CtScreenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CtScreenerApplication.class, args);
    }
}
