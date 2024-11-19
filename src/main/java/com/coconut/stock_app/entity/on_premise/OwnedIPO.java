package com.coconut.stock_app.entity.on_premise;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
public class OwnedIPO {
    @Id
    private Long ownedIPOid;

    private Long quantity;

    private BigDecimal totalPrice;

}
