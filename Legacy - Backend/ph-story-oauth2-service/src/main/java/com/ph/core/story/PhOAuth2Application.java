package com.ph.core.story;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
// @EnableAuthorizationServer
@EnableEncryptableProperties
@ConfigurationPropertiesScan
public class PhOAuth2Application {

    public static void main(String[] args) {
        SpringApplication.run(PhOAuth2Application.class, args);
    }

}
