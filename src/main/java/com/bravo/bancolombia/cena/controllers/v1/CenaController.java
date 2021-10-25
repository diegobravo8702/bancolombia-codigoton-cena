package com.bravo.bancolombia.cena.controllers.v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bravo.bancolombia.cena.services.CenaService;

@RestController
@RequestMapping("/api/v1/organizacion")
public class CenaController {
	private static final Logger logger = LoggerFactory.getLogger(CenaController.class);
	// private static final String PARAM_FILE = "file";

	@Autowired
	CenaService cena;

	@GetMapping
	public ResponseEntity<String> procesarUbicacionGet() {
		logger.info("GET procesarUbicacionGet()");
		return new ResponseEntity<>("Esta es la API de organización de mesas, utilice el metodo POST", HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		logger.info("POST procesarUbicacionPost()");
		String contenido = "";
		String respuesta = "";
		try {
			contenido = new String(file.getBytes(), StandardCharsets.UTF_8);
			logger.info("POST procesarUbicacionPost() - CONTENIDO:");
			logger.info(contenido);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		logger.info("POST procesarUbicacionPost() - procesar...");
		respuesta = cena.getOrganizacion(contenido);
		return new ResponseEntity<>(respuesta, HttpStatus.OK);
	}

}
