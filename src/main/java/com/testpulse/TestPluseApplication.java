package com.testpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TestPluseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestPluseApplication.class, args);
    }

}