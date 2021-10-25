package com.bravo.bancolombia.cena.crypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Decrypt {
	private static final Logger logger = LoggerFactory.getLogger(Decrypt.class);
	private static final String URL = "https://test.evalartapp.com/extapiquest/code_decrypt/";

	public static String decrypt(String encryptCode) {

		String decyptCode = "";
		try {
			// la api de desencripcion es un api rest tipo GET y solo regresa una linea en texto plano.
			URL url = new URL(URL + encryptCode);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod("GET");

			if (huc.getResponseCode() == 200) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(huc.getInputStream()))) {
					decyptCode = reader.readLine();
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return decyptCode.replaceAll("\"", "");
	}

	public static void main(String[] a) {
		System.out.println(decrypt("QzEwMjA5"));
	}

}
