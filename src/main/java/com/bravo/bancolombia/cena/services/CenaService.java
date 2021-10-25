package com.bravo.bancolombia.cena.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bravo.bancolombia.cena.crypt.Decrypt;
import com.bravo.bancolombia.cena.dtos.FiltroDto;
import com.bravo.bancolombia.cena.dtos.MesaDto;
import com.bravo.bancolombia.cena.models.ClientEntity;
import com.bravo.bancolombia.cena.repositories.AccountReadOnlyRepository;
import com.bravo.bancolombia.cena.repositories.ClientReadOnlyRepository;

@Service
public class CenaService {

	private static final Logger logger = LoggerFactory.getLogger(CenaService.class);
	private static short ENCRIPTADO = 1;
	private static short HOMBRE = 1;
	private static short MUJER = 0;

	@Autowired
	AccountReadOnlyRepository cuenta;

	@Autowired
	ClientReadOnlyRepository cliente;

	public String getOrganizacion(String contenido) {
		logger.info("getOrganizacion() ");
		List<MesaDto> mesas = new ArrayList<MesaDto>();
		MesaDto mesaActual = new MesaDto();
		// List<AccountEntity> cuentas = cuenta.findAll();
		// List<ClientEntity> clientes = cliente.findAll();

		StringTokenizer lineas = new StringTokenizer(contenido, "\n");

		// Se evalua cada linea y se procesa
		while (lineas.hasMoreTokens()) {
			String linea = lineas.nextToken();
			if (esMesa(linea)) {
				mesaActual = new MesaDto();
				mesaActual.setNombre(linea.trim());
				mesas.add(mesaActual);
			} else {
				mesaActual.getFiltros().add(obtenerFiltro(linea));
			}
		}

		logger.info("Cantidad de mesas: " + mesas.size());

		poblarCandidatos(mesas);

		StringBuilder resultado = new StringBuilder();
//		for (AccountEntity cuenta : cuentas) {
//			resultado.append(cuenta.toString());
//			resultado.append("\n");
//		}
//		for (ClientEntity cliente : clientes) {
//			resultado.append(cliente.toString());
//			resultado.append("\n");
//		}
		return resultado.toString();
	}

	private boolean esMesa(String texto) {
		Pattern pattern = Pattern.compile("(<*>)");
		Matcher matcher = pattern.matcher(texto);
		return matcher.find();
	}

	private FiltroDto obtenerFiltro(String linea) {
		String[] partes = linea.trim().split(":");
		FiltroDto restriccion = new FiltroDto();
		restriccion.setCodigo(partes[0].trim());
		restriccion.setValor(partes[1].trim());
		return restriccion;

	}

	private void poblarCandidatos(List<MesaDto> mesas) {
		logger.info("poblarCandidatos");

		// TODO: asumo que se debe hacer esta validacion: un invitado de una mesa no debe ser evaluado en posteriores mesas
		// candidatos en mesas anteriores para ser excluidos de las nuevas mesas

		ArrayList<ClientEntity> candidatosConfirmados = new ArrayList<ClientEntity>();
		// ArrayList<ClientEntity> candidatosConfirmados = (ArrayList<ClientEntity>) cliente.findByCode("C10001");

		// Con cada una de las restricciones se va excluyendo candidatos que no cumplan con el criterio

		for (MesaDto mesa : mesas) {
			logger.info("poblarCandidatos - mesa - " + mesa.getNombre());

			// Se obtienen los candidatos iniciales por mesa
			ArrayList<ClientEntity> candidatosInicialesPorMesa = (ArrayList<ClientEntity>) cliente.findAll();
			ArrayList<ClientEntity> candidatosAptosPorFiltro = null;

			logger.info("poblarCandidatos - ANTES DE APLICAR EL FILTRO LOS CANDIDATOS SON: [" + candidatosInicialesPorMesa.size() + "]");
//			for (ClientEntity candidato : candidatosInicialesPorMesa) {
//				logger.info(candidato.toString());
//			}
			/*
			 * // --- quitar desde aqui for (ClientEntity candidato : candidatosInicialesPorMesa) { if (candidato.getEncrypt() == ENCRIPTADO) { candidato.setCodeDecript(Decrypt.decrypt(candidato.getCode())); }
			 * else { candidato.setCodeDecript(candidato.getCode()); } } Collections.sort(candidatosInicialesPorMesa, new Comparator<ClientEntity>() {
			 * 
			 * @Override public int compare(ClientEntity c1, ClientEntity c2) { return c1.getCodeDecript().compareTo(c2.getCodeDecript()); } });
			 * 
			 * logger.info("poblarCandidatos - ORDENADOS: [" + candidatosInicialesPorMesa.size() + "]"); for (ClientEntity candidato : candidatosInicialesPorMesa) { logger.info(candidato.toString()); } // ---
			 * quitar hasta aqui
			 */

			// Se excluyen candidatos ya confirmados en otras mesas
			int cantidadCandidatosPorMesaAntesDeExcluir = candidatosInicialesPorMesa.size();
			candidatosInicialesPorMesa.removeIf(c -> (candidatosConfirmados.contains(c)));
			if (cantidadCandidatosPorMesaAntesDeExcluir != candidatosInicialesPorMesa.size()) {
				logger.info("###########################################");
				logger.info("########################################### - Excluidos porque ya se habian confirmado en otra mesa");
				logger.info("###########################################");
			}

			logger.info("poblarCandidatos - DESPUES DE ExCLUIR LOS CANDIDATOS CONFIRMADOS: [" + candidatosInicialesPorMesa.size() + "]");
//			for (ClientEntity candidato : candidatosInicialesPorMesa) {
//				logger.info(candidato.toString());
//			}

			for (FiltroDto restriccion : mesa.getFiltros()) {
				logger.info("poblarCandidatos - mesa - " + mesa.getNombre() + " - restriccion - " + restriccion.getCodigo() + " : " + restriccion.getValor());

				// Se procesa la restriccion de Tipo de Cliente y Ubicación Geografica
				switch (restriccion.getCodigo()) {
				// case TIPO_CLIENTE:
				case "TC":
					logger.info("poblarCandidatos - mesa - " + mesa.getNombre() + " - restriccion - " + restriccion.getCodigo() + " : " + restriccion.getValor() + " procesando TIPO_CLIENTE");
					candidatosAptosPorFiltro = (ArrayList<ClientEntity>) cliente.findByType(Integer.valueOf(restriccion.getValor()));
					logger.info("poblarCandidatos - mesa - " + mesa.getNombre() + " - restriccion - " + restriccion.getCodigo() + " : " + restriccion.getValor() + " procesando TIPO_CLIENTE - encontrados : " + candidatosAptosPorFiltro.size());

					// removiendo de los candidatos aquellos que no fueron encontrados en la restriccion
					candidatosInicialesPorMesa = new ArrayList<ClientEntity>(candidatosInicialesPorMesa.stream().distinct().filter(candidatosAptosPorFiltro::contains).collect(Collectors.toSet()));

					break;
				// case UBICACION_GEOGRAFICA:
				case "UG":
					logger.info("poblarCandidatos - mesa - " + mesa.getNombre() + " - restriccion - " + restriccion.getCodigo() + " : " + restriccion.getValor() + " procesando UBICACION_GEOGRAFICA");
					candidatosAptosPorFiltro = (ArrayList<ClientEntity>) cliente.findByLocation(restriccion.getValor());
					logger.info("poblarCandidatos - mesa - " + mesa.getNombre() + " - restriccion - " + restriccion.getCodigo() + " : " + restriccion.getValor() + " procesando UBICACION_GEOGRAFICA - encontrados : " + candidatosAptosPorFiltro.size());

					candidatosInicialesPorMesa = new ArrayList<ClientEntity>(candidatosInicialesPorMesa.stream().distinct().filter(candidatosAptosPorFiltro::contains).collect(Collectors.toSet()));

					break;

				case "RI":
					logger.info("poblarCandidatos - mesa - " + mesa.getNombre() + " - restriccion - " + restriccion.getCodigo() + " : " + restriccion.getValor() + " procesando RANGO_INICIAL");
					// Se eliminan las cuentas cuyo monto sea inferior al rango inicial
					// TODO: pedir informacion relacionada con que los montos sean inclusivos o exclusivos

					// -1, 0, or 1 as this BigDecimal is numerically less than, equal to, or greater than val.
					candidatosInicialesPorMesa.removeIf(c -> (c.getBalance().compareTo(new BigDecimal(restriccion.getValor())) < 0));

					break;

				case "RF":
					logger.info("poblarCandidatos - mesa - " + mesa.getNombre() + " - restriccion - " + restriccion.getCodigo() + " : " + restriccion.getValor() + " procesando RANGO_FINAL");
					// Se eliminan las cuentas cuyo monto sea superior al rango final
					// TODO: pedir informacion relacionada con que los montos sean inclusivos o exclusivos

					// -1, 0, or 1 as this BigDecimal is numerically less than, equal to, or greater than val.
					candidatosInicialesPorMesa.removeIf(c -> (c.getBalance().compareTo(new BigDecimal(restriccion.getValor())) > 0));

					break;

				}
				logger.info("poblarCandidatos - mesa - " + mesa.getNombre() + " - restriccion - " + restriccion.getCodigo() + " : " + restriccion.getValor() + " DESPUES DE APLICAR EL FILTRO LOS CANDIDATOS SON: [" + candidatosInicialesPorMesa.size() + "]");
//				for (ClientEntity candidato : candidatosInicialesPorMesa) {
//					logger.info(candidato.toString());
//				}

			}

			// Logica de ordenamiento:
			// es posible quye haya codigos encriptados por lo que se evalua si alguno de los registros contiene el codigo encriptado y se procede a desencriptar
			// La espedificacion indica que se ordena por balance y en caso de empate se ordena por codigo
			// en este caso ordenare primero por codigo y despues se ordena por balance

			for (ClientEntity candidato : candidatosInicialesPorMesa) {
				if (candidato.getEncrypt() == ENCRIPTADO) {
					candidato.setCodeDecript(Decrypt.decrypt(candidato.getCode()));
				} else {
					candidato.setCodeDecript(candidato.getCode());
				}
			}

			// Ya se ha poblado los codigos, se procede con los ordenamientos
			// Ordenamiento 1: por codigo. ASCENDENTE
			Collections.sort(candidatosInicialesPorMesa, new Comparator<ClientEntity>() {
				@Override
				public int compare(ClientEntity c1, ClientEntity c2) {
					return c1.getCodeDecript().compareTo(c2.getCodeDecript());
				}
			});

			// Ordenamiento 2: por sma total de balance. DESCENDENTE
			Collections.sort(candidatosInicialesPorMesa, new Comparator<ClientEntity>() {
				@Override
				public int compare(ClientEntity c1, ClientEntity c2) {
					return c2.getBalance().compareTo(c1.getBalance());
				}
			});

			// ya ordenados se procede con la seleccion de la mesa

			int hombresEncontrados = 0;
			int mujeresEncontradas = 0;
			int requisitoPorSexo[] = { 0, 0 };
			ArrayList<String> empresasInvitadas = new ArrayList<String>();
			// ciclo buscando kos candidatos
			boolean busquedaCompletada = false;
			while (!busquedaCompletada) {
				// validacion: si aun hay candidatos por evaluar
				if (candidatosInicialesPorMesa.isEmpty()) {
					logger.info("ya no hay nadie mas por evaluar");
					busquedaCompletada = true;
					continue;
				}

				ClientEntity candidato = candidatosInicialesPorMesa.get(0);
				candidatosInicialesPorMesa.remove(0);
				logger.info("candidato evaluado: " + candidato.toString());

				// validacion: si el candidato es apto por la cuota de sexo
				if (requisitoPorSexo[candidato.getMale()] >= 4) {
					logger.info("descartado por sexo");
					continue;
				}

				// Se elimina de la lista de candidatos aquellos que pertenecen a la misma empresa
				int cantidadCandidatosPorMesaAntesDeExcluir_2 = candidatosInicialesPorMesa.size();
				candidatosInicialesPorMesa.removeIf(c -> (c.getCompany().equalsIgnoreCase(candidato.getCompany())));
				if (cantidadCandidatosPorMesaAntesDeExcluir_2 != candidatosInicialesPorMesa.size()) {
					logger.info("###########################################");
					logger.info("########################################### - Excluidos porque pertenecen a la misma empresa");
					logger.info("###########################################");
				}

				// se confirma al candidato como invitado
				requisitoPorSexo[candidato.getMale()]++;
				mesa.getInvitados().add(candidato);

			}

			logger.info("### MESA COMPLETADA");
			logger.info("### MESA COMPLETADA");
			logger.info("### MESA COMPLETADA");
			logger.info("### MESA COMPLETADA");
			for (ClientEntity invitado : mesa.getInvitados()) {
				logger.info("### MESA COMPLETADA - invitado: " + invitado);
			}

		}

	}

	public static void main(String[] a) {
		String texto = "<General>";
		Pattern pattern = Pattern.compile("<*>");
		Matcher matcher = pattern.matcher(texto);
		logger.info("" + matcher.find());

		// test de eliminacion de candidato previamente confirmado
		ClientEntity c1 = new ClientEntity();
		c1.setCode("A");
		ClientEntity c2 = new ClientEntity();
		c2.setCode("B");
		ClientEntity c3 = new ClientEntity();
		c3.setCode("C");
		ClientEntity c4 = new ClientEntity();
		c4.setCode("D");

		ArrayList<ClientEntity> candidatosConfirmados = new ArrayList<ClientEntity>();
		candidatosConfirmados.add(c1);
		candidatosConfirmados.add(c2);

		ArrayList<ClientEntity> candidatosInicialesPorMesa = new ArrayList<ClientEntity>();

		candidatosInicialesPorMesa.add(c1);
		candidatosInicialesPorMesa.add(c2);
		candidatosInicialesPorMesa.add(c3);
		candidatosInicialesPorMesa.add(c4);

		for (ClientEntity candidato : candidatosInicialesPorMesa) {

			if (candidatosConfirmados.contains(candidato)) {
				logger.info("evaluando a: " + candidato.toString() + "   :::    ENCONTRADO");
			} else {
				logger.info("evaluando a: " + candidato.toString() + "   :::    NO ENCONTRADO");
			}

		}

		candidatosInicialesPorMesa.removeIf(c -> (candidatosConfirmados.contains(c)));

		logger.info("DESPUES DE ELIMINAR:");

		for (ClientEntity candidato : candidatosInicialesPorMesa) {
			logger.info(candidato.toString());
		}

	}

}
