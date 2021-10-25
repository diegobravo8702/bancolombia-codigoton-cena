package com.bravo.bancolombia.cena.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "account")
@Data
@ToString(includeFieldNames = true , exclude = "client" )
public class AccountEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false)
	private BigDecimal balance;

	@ManyToOne
	private ClientEntity client;

}