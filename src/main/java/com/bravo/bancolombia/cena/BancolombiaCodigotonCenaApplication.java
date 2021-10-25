package com.bravo.bancolombia.cena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.bravo.bancolombia.cena.properties.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })
public class BancolombiaCodigotonCenaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BancolombiaCodigotonCenaApplication.class, args);
	}

}
