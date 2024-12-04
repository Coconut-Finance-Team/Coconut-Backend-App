package com.coconut.stock_app.entity.on_premise;


import com.coconut.stock_app.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "owned_ipo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnedIPO extends BaseEntity {
    @Id
    private Long ownedIPOid;

    private Long quantity;

    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId")
    private Account account;

}
