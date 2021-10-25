package com.bravo.bancolombia.cena.dtos;

import java.util.ArrayList;
import java.util.List;

public class MesaDto {
	private String nombre;
	private List<FiltroDto> filtros;
	private List<String> invitados;

	public MesaDto() {
		this.nombre = "UNDEFINED";
		this.filtros = new ArrayList<FiltroDto>();
		this.invitados = new ArrayList<String>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<FiltroDto> getFiltros() {
		return filtros;
	}

	public void setFiltros(List<FiltroDto> filtros) {
		this.filtros = filtros;
	}

	public List<String> getInvitados() {
		return invitados;
	}

	public void setInvitados(List<String> invitados) {
		this.invitados = invitados;
	}

}
