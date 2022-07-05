package com.spring.twitter.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class TwitterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterApplication.class, args);
	}
}
