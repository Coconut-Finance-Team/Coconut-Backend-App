package com.coconut.stock_app.repository.cloud;

import com.coconut.stock_app.entity.cloud.IPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IPORepository extends JpaRepository<IPO, Long> {
    @Query("""
        SELECT i
        FROM IPO i
        WHERE :currentDate BETWEEN i.subscriptionStartDate AND i.subscriptionEndDate
       """)
    List<IPO> findActiveIPOs(LocalDate currentDate);
}
