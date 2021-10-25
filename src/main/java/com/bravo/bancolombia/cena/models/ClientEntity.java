package com.bravo.bancolombia.cena.models;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "client")
@Data
@ToString
public class ClientEntity {
	private static final Logger logger = LoggerFactory.getLogger(ClientEntity.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column
	private String code;
	
	@Transient
	private String codeDecript;

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

	@OneToMany(mappedBy = "client")
	private List<AccountEntity> accounts;
	
	@Transient
	@Getter(value = AccessLevel.NONE)
	private BigDecimal balance;

	public BigDecimal getBalance() {
//		logger.info("getBalance()");
		BigDecimal balanceTotal = new BigDecimal(0);
		if(accounts != null && !accounts.isEmpty()) {
			for (AccountEntity cuenta : accounts) {
//				logger.info("getBalance() - cuenta " + cuenta.toString() + " :::: " + cuenta.getBalance());
//				logger.info("EN GETTER SUMATORIA " + cuenta.toString());
				balanceTotal = balanceTotal.add(cuenta.getBalance());
			}
		}else {
//			logger.info("getBalance() - sin datos");
		}
//		logger.info("getBalance() - total sumado: " + balanceTotal);
		return balanceTotal;
	}


	
	
	
	
	
	
}
