package com.bravo.bancolombia.cena.bancolombiacodigotoncena.controllers.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organizacion")
public class CenaController {

	@GetMapping
	public ResponseEntity<String> procesarUbicacion() {
		System.out.println("procesarUbicacion ");
		String resultado = "Esta es solo una versión preliminar";
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

}
