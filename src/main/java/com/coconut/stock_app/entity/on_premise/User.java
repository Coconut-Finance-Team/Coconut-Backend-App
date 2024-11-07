package com.coconut.stock_app.entity.on_premise;

import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private UsersAccountStatusEnum accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsersRoleEnum role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    @OneToOne
    @JoinColumn(name = "primary_account_id", referencedColumnName = "accountId")
    private Account primaryAccount;
}



enum UsersRoleEnum {
    ADMIN,
    USER
}
enum UsersAccountStatusEnum {
    ACTIVE,
    INACTIVE,
    SUSPENDED
}