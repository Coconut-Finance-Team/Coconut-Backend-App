package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @EntityGraph(attributePaths = {"ownedStocks"})
    Optional<Account> findByAccountUuid(String accountUuid);

    boolean existsByAccountId(String accountId);
}
