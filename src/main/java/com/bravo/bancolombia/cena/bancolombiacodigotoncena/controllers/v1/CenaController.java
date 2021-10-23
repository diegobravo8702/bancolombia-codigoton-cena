package com.bravo.bancolombia.cena.bancolombiacodigotoncena.controllers.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bravo.bancolombia.cena.bancolombiacodigotoncena.services.CenaService;

@RestController
@RequestMapping("/api/v1/organizacion")
public class CenaController {
	
	@Autowired
	CenaService cena;

	@GetMapping
	public ResponseEntity<String> procesarUbicacion() {
		System.out.println("procesarUbicacion()");
		return new ResponseEntity<>(cena.getOrganizacion(), HttpStatus.OK);
	}

}
