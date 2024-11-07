package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.ProfitLoss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfitLossRepository extends JpaRepository<ProfitLoss, Long> {
}
