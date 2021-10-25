package com.bravo.bancolombia.cena.repositories;

import java.util.List;

import com.bravo.bancolombia.cena.models.AccountEntity;

public interface AccountReadOnlyRepository extends ReadOnlyRepository<AccountEntity, Integer> {
	List<AccountEntity> findByClientId(Integer clientId);
}
