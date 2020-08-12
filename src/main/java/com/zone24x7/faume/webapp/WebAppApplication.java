package com.zone24x7.faume.webapp;

import com.zone24x7.faume.webapp.service.FilesStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class WebAppApplication{

	public static void main(String[] args) {
		SpringApplication.run(WebAppApplication.class, args);
	}

}
