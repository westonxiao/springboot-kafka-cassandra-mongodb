package com.healthcare.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;

public class PriceUpdate {
    private String sku;
    private BigDecimal newPrice;

    // Add validation if needed
    public boolean isValid() {
        return sku != null && newPrice != null && newPrice.compareTo(BigDecimal.ZERO) > 0;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }
}
