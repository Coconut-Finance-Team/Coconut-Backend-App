package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.OwnedIPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnedIPORepository extends JpaRepository<OwnedIPO, Long> {

    @Query("""
        SELECT o
        FROM OwnedIPO o
        JOIN FETCH o.account a
        WHERE a.accountUuid = :accountUuid
        ORDER BY o.createdAt DESC
    """)
    List<OwnedIPO> findAllByAccountUuid(String accountUuid);
}
