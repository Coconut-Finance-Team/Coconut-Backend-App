package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
