package com.uci.expertConnect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpertConnectApplication {
	private static final Logger logger = LoggerFactory.getLogger(ExpertConnectApplication.class);

	public static void main(String[] args) {
		logger.info("Starting ExpertConnect application...");
		SpringApplication.run(ExpertConnectApplication.class, args);
		logger.info("ExpertConnect application started successfully!");
	}

}
