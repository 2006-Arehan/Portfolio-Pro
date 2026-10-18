package com.portfoliopro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * PortfolioPro - Main Application Entry Point
 * 
 * Stock Trading & Portfolio Management System
 * Submission Project for HCL
 */
@SpringBootApplication
@EnableScheduling
public class PortfolioProApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioProApplication.class, args);
        System.out.println("=========================================================");
        System.out.println(" PortfolioPro: Stock Trading & Portfolio Management      ");
        System.out.println(" Server successfully running at: http://localhost:8080   ");
        System.out.println(" H2 Database Console at:        http://localhost:8080/h2-console ");
        System.out.println(" HCL Project Submission Ready!                           ");
        System.out.println("=========================================================");
    }
}
