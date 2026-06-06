package com.ph.core.story;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        // Enabling Spring Cloud Config Client
        "spring.cloud.config.enabled=true",
        // Allowing the application to start even if the Config Server is not available
        "spring.config.import=optional:configserver:",
        // Pointing to the local Config Server
        "spring.cloud.config.uri=http://localhost:9396",
        // Setting the application name and active profile for Config Server
        "spring.application.name=ph-story-seaweedfs-service",
        // Using 'dev' profile to load development-specific configurations
        "spring.profiles.active=dev",
        // Excluding auto-configurations that are not relevant for this service
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"})
class PhSeaweedFsServiceApplicationTests {

    @Test
    void contextLoads() {}

}
