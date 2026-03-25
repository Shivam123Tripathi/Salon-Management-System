package com.salon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ENTRY POINT of the entire application.
 * 
 * @SpringBootApplication is a powerful annotation that combines three things:
 * 1. @Configuration - This class can define beans (objects managed by Spring)
 * 2. @EnableAutoConfiguration - Spring Boot auto-configures based on dependencies
 *    (e.g., sees MySQL driver → configures DataSource automatically)
 * 3. @ComponentScan - Scans all packages under "com.salon" to find @Controller,
 *    @Service, @Repository, @Component classes and register them
 * 
 * When you run this class, Spring Boot:
 * 1. Starts an embedded Tomcat server on port 8080
 * 2. Connects to MySQL and creates/updates tables
 * 3. Registers all your controllers, services, repositories
 * 4. Your API is now live and ready to accept requests!
 */
@SpringBootApplication
public class SalonApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalonApplication.class, args);
    }
}
