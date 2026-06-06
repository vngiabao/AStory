package com.ph.core.story;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableEncryptableProperties
@ConfigurationPropertiesScan
@EnableScheduling
public class PhMvpServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhMvpServiceApplication.class, args);
    }

}

