package com.peoplesoft.scraper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "com.peoplesoft.scraper.Controller","com.peoplesoft.scraper.DAO","com.peoplesoft.scraper.model"})
@SpringBootApplication
public class ScraperApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScraperApplication.class, args);
	}
}
