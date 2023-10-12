package com.eversync.integration.s3filehandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class S3filehandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(S3filehandlerApplication.class, args);
	}

}
