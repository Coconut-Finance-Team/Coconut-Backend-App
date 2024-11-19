package com.coconut.stock_app.entity.on_premise;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, length = 36)
    private String userUuid;

    @Column(unique = true, nullable = false, length = 50)
    private String id; // 사용자 계정 ID

    @Column(nullable = false, length = 100)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 10)
    private String gender;

    private String job;

    private String investmentStyle;

    private LocalDate birthdate;

    private String phone;

    private String socialSecurityNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    @OneToOne
    @JoinColumn(name = "primary_account_id", referencedColumnName = "accountId")
    private Account primaryAccount;

    public User(String email) {
        this.email = email;
    }
}
