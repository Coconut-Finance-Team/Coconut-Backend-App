package com.coconut.stock_app.repository.on_premis;

import com.coconut.stock_app.entity.on_premis.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
