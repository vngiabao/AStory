package com.ph.core.story.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class PhDiscoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhDiscoveryServerApplication.class, args);
	}

}
