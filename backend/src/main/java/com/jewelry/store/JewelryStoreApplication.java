package com.jewelry.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Entry Point for Jewelry E-Commerce Management System.
 * 
 * Enabled Features:
 * - Component Scanning across com.jewelry.store
 * - Auto-Configuration for Spring MVC, Data JPA, Security, Actuator
 * - JPA Auditing for automatic @CreatedDate and @LastModifiedDate tracking
 */
@SpringBootApplication
@EnableJpaAuditing
public class JewelryStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(JewelryStoreApplication.class, args);
    }
}
