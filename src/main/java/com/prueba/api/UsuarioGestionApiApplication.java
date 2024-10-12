package com.prueba.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.prueba.api.controller")
public class UsuarioGestionApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsuarioGestionApiApplication.class, args);
	}

}
