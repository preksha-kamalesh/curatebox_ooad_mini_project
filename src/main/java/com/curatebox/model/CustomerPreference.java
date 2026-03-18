package com.curatebox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_preferences")
public class CustomerPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "preference_option_id", nullable = false)
    private PreferenceOption preferenceOption;

    @Column(name = "is_like")
    private boolean liked;

    public boolean isDislike() {
        return !liked;
    }

    public String getPreferenceValue() {
        return preferenceOption != null ? preferenceOption.getPreferenceValue() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public PreferenceOption getPreferenceOption() {
        return preferenceOption;
    }

    public void setPreferenceOption(PreferenceOption preferenceOption) {
        this.preferenceOption = preferenceOption;
    }

    public boolean isLike() {
        return liked;
    }

    public void setLike(boolean like) {
        this.liked = like;
    }
}
