package com.bravo.bancolombia.cena.bancolombiacodigotoncena.repositories;

import java.util.List;

import com.bravo.bancolombia.cena.bancolombiacodigotoncena.models.ClientEntity;

public interface ClientReadOnlyRepository extends ReadOnlyRepository<ClientEntity, Integer> {
	List<ClientEntity> findByCode(String code);
	List<ClientEntity> findByMale(Short male);
	List<ClientEntity> findByEncrypt(Short encrypt);
	List<ClientEntity> findByType(Integer type);
	List<ClientEntity> findByLocation(String location);
	
}
