package com.coconut.stock_app.repository.cloud;

import com.coconut.stock_app.entity.cloud.IPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPORepository extends JpaRepository<IPO, Long> {
}
