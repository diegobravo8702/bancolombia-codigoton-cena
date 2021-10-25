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

		return procesarCandidatos(mesas);
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

	private String procesarCandidatos(List<MesaDto> mesas) {
		logger.info("poblarCandidatos");

		// TODO: asumo que se debe hacer esta validacion: un invitado de una mesa no debe ser evaluado en posteriores mesas
		// candidatos en mesas anteriores para ser excluidos de las nuevas mesas

		ArrayList<ClientEntity> candidatosConfirmadosCena = new ArrayList<ClientEntity>();
		// ArrayList<ClientEntity> candidatosConfirmados = (ArrayList<ClientEntity>) cliente.findByCode("C10001");

		// Con cada una de las restricciones se va excluyendo candidatos que no cumplan con el criterio

		for (MesaDto mesa : mesas) {
			logger.info("poblarCandidatos - mesa - " + mesa.getNombre());

			// Se obtienen los candidatos iniciales por mesa
			ArrayList<ClientEntity> candidatosInicialesPorMesa = (ArrayList<ClientEntity>) cliente.findAll();
			ArrayList<ClientEntity> candidatosAptosPorFiltro = null;

			// Se excluyen candidatos ya confirmados en otras mesas
			int cantidadCandidatosPorMesaAntesDeExcluir = candidatosInicialesPorMesa.size();
			candidatosInicialesPorMesa.removeIf(c -> (candidatosConfirmadosCena.contains(c)));
			if (cantidadCandidatosPorMesaAntesDeExcluir != candidatosInicialesPorMesa.size()) {
				logger.info("########################################### - Excluidos porque ya se habian confirmado en otra mesa");
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
			// Ordenamiento 1: por codigo. DESCENDENTE
			Collections.sort(candidatosInicialesPorMesa, new Comparator<ClientEntity>() {
				@Override
				public int compare(ClientEntity c1, ClientEntity c2) {
					return c2.getCodeDecript().compareTo(c1.getCodeDecript());
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
			// ciclo buscando los candidatos
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
					logger.info("########################################### - Excluidos porque pertenecen a la misma empresa");
				}

				// se registra el nuevo invitado en el contador de candidatos por genero
				requisitoPorSexo[candidato.getMale()]++;
				// se confirma al candidato como invitado en la mesa
				mesa.getInvitados().add(candidato);
				// se confirma al candidato como invitado a la cena
				candidatosConfirmadosCena.add(candidato);

			}

			// Se ha completado el proceso, de invitados, Se requiere validar si la cantidad de hombres y mujeres fue la misma
			// Si la cantidad de hombres y mujeres es distinta se eliminan de los invitados la cantidad de integrantes del sexo opuesto faltantes

			int diferencia = requisitoPorSexo[HOMBRE] - requisitoPorSexo[MUJER];
			logger.info("         ***   diferencia: " + diferencia);
			ArrayList<ClientEntity> candidatosPorCancelar = new ArrayList<ClientEntity>();
			short sexoPorEliminar = -1;
			if (diferencia < 0) {
				sexoPorEliminar = MUJER;
			} else if (diferencia > 0) {
				sexoPorEliminar = HOMBRE;
			}
			diferencia = Math.abs(diferencia);
			if (diferencia != 0) {
				// Se procede a seleccionar a los que seran desinvitados
				logger.info("Se procede a eliminar o desinvitar a [" + diferencia + "]");

				// La evaluacion de la posicion es en orden inverso.
				// se guarda la posicion del eliminado mas reciente para que el la siguiente iteracion se comience desde un indice anterior.
				// en la primera iteracion no se ha eliminado a nadie por lo que se comienza desde el ultimo
				int posicionEliminadoAnterior = mesa.getInvitados().size() - 1;
				for (int cantidad = 0; cantidad < diferencia; cantidad++) {
					// cada iteracion es una eliminacion

					for (int posicion = posicionEliminadoAnterior; posicion > 0; posicion--) {
						// cada iteracion es un invitado evaluandose para ser eliminado
						// se itera en orden inverso para excluir primero a los de balance mas bajo
						ClientEntity invitadoEvaluado = mesa.getInvitados().get(posicion);
						if (invitadoEvaluado.getMale() == sexoPorEliminar) {
							// Encontrado el invitado que será desinvitado jeje
							logger.info("Se desinvitara a " + invitadoEvaluado.toString());
							candidatosPorCancelar.add(invitadoEvaluado);
							// dado que si se encontro a quien eliminar, se actualiza la posicion para siguietnes iteraciones
							posicionEliminadoAnterior = posicion;
							// se finaliza el ciclo

							break;

						}
					}
				}
			}

			// ya se sabe quien sera desinvitado
			for (ClientEntity candidato : candidatosPorCancelar) {
				// Se elimina de la mesa
				mesa.getInvitados().remove(candidato);
				// Se elimina de los invitados confirmados para que pueda ser invitado a otra mesa

			}

			logger.info("### MESA COMPLETADA");
			for (ClientEntity invitado : mesa.getInvitados()) {
				logger.info("### MESA COMPLETADA - invitado: " + invitado);
			}

		}

		logger.info("Resultado final: ");
		StringBuilder resultadoFinal = new StringBuilder();

		for (MesaDto mesa : mesas) {
			resultadoFinal.append(mesa.getNombre() + "\n");
			if (mesa.getInvitados().size() < 4) {
				resultadoFinal.append("CANCELADA");
			} else {
				boolean primero = true;
				for (ClientEntity invitado : mesa.getInvitados()) {
					if (primero) {
						primero = false;
						resultadoFinal.append(invitado.getCodeDecript());
						// resultadoFinal.append("" + (invitado.getMale() == HOMBRE ? "h" : "m") + invitado.getCodeDecript());
					} else {
						resultadoFinal.append("," + invitado.getCodeDecript());
						// resultadoFinal.append("," + (invitado.getMale() == HOMBRE ? "h" : "m") + invitado.getCodeDecript());
					}
				}
			}
			resultadoFinal.append("\n");

		}
		logger.info(resultadoFinal.toString());
		return resultadoFinal.toString();

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
