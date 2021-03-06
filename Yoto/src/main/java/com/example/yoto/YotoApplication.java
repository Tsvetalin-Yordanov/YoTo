package com.example.yoto;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@EnableWebSecurity
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class YotoApplication {

    public static void main(String[] args) {
        SpringApplication.run(YotoApplication.class, args);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
