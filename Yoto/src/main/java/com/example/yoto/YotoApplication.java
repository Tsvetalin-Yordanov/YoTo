package com.example.yoto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class YotoApplication {

    public static void main(String[] args) {
        SpringApplication.run(YotoApplication.class, args);
    }

}
