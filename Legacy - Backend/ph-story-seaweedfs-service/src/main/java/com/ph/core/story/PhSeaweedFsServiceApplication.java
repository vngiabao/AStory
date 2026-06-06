package com.ph.core.story;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableEncryptableProperties
@ConfigurationPropertiesScan
public class PhSeaweedFsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhSeaweedFsServiceApplication.class, args);
    }

}

