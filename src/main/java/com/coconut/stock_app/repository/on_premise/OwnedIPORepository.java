package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.OwnedIPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnedIPORepository extends JpaRepository<OwnedIPO, Long> {
}
