package com.curatebox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "monthly_boxes")
public class MonthlyBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boxId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private LocalDate curationDate;
    private String shippingStatus;
    private LocalDate shippedAt;

    @OneToMany(mappedBy = "monthlyBox", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoxContent> boxContents = new ArrayList<>();

    public void addProduct(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        for (BoxContent existing : boxContents) {
            if (existing.getProduct() != null && existing.getProduct().getProductId() != null
                    && existing.getProduct().getProductId().equals(product.getProductId())) {
                existing.incrementQuantity(quantity);
                return;
            }
        }

        BoxContent content = new BoxContent();
        content.setMonthlyBox(this);
        content.setProduct(product);
        content.setQuantity(quantity);
        this.boxContents.add(content);
    }

    public void updateShippingStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Shipping status is required");
        }
        this.shippingStatus = status.trim().toUpperCase(Locale.ROOT);
        if ("SHIPPED".equals(this.shippingStatus) && this.shippedAt == null) {
            this.shippedAt = LocalDate.now();
        }
    }

    public void ship() {
        this.shippingStatus = "SHIPPED";
        this.shippedAt = LocalDate.now();
    }

    public Long getBoxId() {
        return boxId;
    }

    public void setBoxId(Long boxId) {
        this.boxId = boxId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDate getCurationDate() {
        return curationDate;
    }

    public void setCurationDate(LocalDate curationDate) {
        this.curationDate = curationDate;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public LocalDate getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDate shippedAt) {
        this.shippedAt = shippedAt;
    }

    public List<BoxContent> getBoxContents() {
        return boxContents;
    }

    public void setBoxContents(List<BoxContent> boxContents) {
        this.boxContents = boxContents;
    }
}
