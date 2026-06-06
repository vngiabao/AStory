package com.ph.core.story;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class PhApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhApiGatewayApplication.class, args);
    }
}
