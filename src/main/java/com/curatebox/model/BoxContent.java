package com.curatebox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "box_contents")
public class BoxContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boxContentId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "box_id", nullable = false)
    private MonthlyBox monthlyBox;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    public void updateQuantity(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = qty;
    }

    public void incrementQuantity(int delta) {
        if (delta <= 0) {
            throw new IllegalArgumentException("Quantity increment must be positive");
        }
        this.quantity += delta;
    }

    public Long getBoxContentId() {
        return boxContentId;
    }

    public void setBoxContentId(Long boxContentId) {
        this.boxContentId = boxContentId;
    }

    public MonthlyBox getMonthlyBox() {
        return monthlyBox;
    }

    public void setMonthlyBox(MonthlyBox monthlyBox) {
        this.monthlyBox = monthlyBox;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        updateQuantity(quantity);
    }
}
