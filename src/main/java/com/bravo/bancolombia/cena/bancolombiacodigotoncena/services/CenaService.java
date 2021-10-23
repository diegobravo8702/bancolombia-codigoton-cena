package com.bravo.bancolombia.cena.bancolombiacodigotoncena.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bravo.bancolombia.cena.bancolombiacodigotoncena.models.AccountEntity;
import com.bravo.bancolombia.cena.bancolombiacodigotoncena.models.ClientEntity;
import com.bravo.bancolombia.cena.bancolombiacodigotoncena.repositories.AccountReadOnlyRepository;
import com.bravo.bancolombia.cena.bancolombiacodigotoncena.repositories.ClientReadOnlyRepository;

@Service
public class CenaService {

	@Autowired
	AccountReadOnlyRepository cuenta;

	@Autowired
	ClientReadOnlyRepository cliente;

	public String getOrganizacion() {
		List<AccountEntity> cuentas = cuenta.findAll();
		List<ClientEntity> clientes = cliente.findAll();

		StringBuilder resultado = new StringBuilder();
		for (AccountEntity cuenta : cuentas) {
			resultado.append(cuenta.toString());
			resultado.append("\n");
		}
		for (ClientEntity cliente : clientes) {
			resultado.append(cliente.toString());
			resultado.append("\n");
		}
		return resultado.toString();
	}
}
