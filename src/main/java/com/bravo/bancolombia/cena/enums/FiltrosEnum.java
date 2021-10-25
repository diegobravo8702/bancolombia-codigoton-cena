package com.bravo.bancolombia.cena.enums;

public enum FiltrosEnum {
	TIPO_CLIENTE("TC"), UBICACION_GEOGRAFICA("UG"), BALANCE_INICIAL("RI"), BALANCE_FINAL("RF");

	private final String valor;

	FiltrosEnum(String valor) {
		this.valor = valor;
	}

	public String getValor() {
		return this.valor;
	}

	public static FiltrosEnum buscarPorValor(String valor) {
		return FiltrosEnum.valueOf(valor);
	}

}
