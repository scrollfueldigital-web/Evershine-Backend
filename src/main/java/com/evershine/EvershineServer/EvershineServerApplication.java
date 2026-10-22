package com.evershine.EvershineServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EvershineServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvershineServerApplication.class, args);
	}

}
