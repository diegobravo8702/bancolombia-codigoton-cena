package com.bravo.bancolombia.cena.bancolombiacodigotoncena.repositories;

import java.util.List;

import com.bravo.bancolombia.cena.bancolombiacodigotoncena.models.AccountEntity;

public interface AccountReadOnlyRepository extends ReadOnlyRepository<AccountEntity, Integer> {
	List<AccountEntity> findByClientId(Integer clientId);
}
