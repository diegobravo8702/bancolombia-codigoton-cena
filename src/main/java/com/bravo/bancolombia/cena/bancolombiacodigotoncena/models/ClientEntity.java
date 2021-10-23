package com.bravo.bancolombia.cena.bancolombiacodigotoncena.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "client")
@Data
@ToString(includeFieldNames = true)
public class ClientEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column
	private String code;

	@Column
	private Short male;

	@Column
	private Integer type;

	@Column
	private String company;
	
	@Column
	private String location;

	@Column
	private Short encrypt;
}
