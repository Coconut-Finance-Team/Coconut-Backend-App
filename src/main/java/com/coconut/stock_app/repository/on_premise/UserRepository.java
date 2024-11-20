package com.coconut.stock_app.repository.on_premise;

import com.coconut.stock_app.entity.on_premise.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email); // 이메일 중복 확인
    Optional<User> findByUsernameAndPhoneAndSocialSecurityNumber(String username, String phone, String socialSecurityNumber);
    Optional<User> findByUserUuid(String uuid);
}
