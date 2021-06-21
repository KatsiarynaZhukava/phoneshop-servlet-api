package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order extends Cart {
    private Long id;
    private String secureId;
    private BigDecimal subtotal;
    private BigDecimal deliveryCost;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate deliveryDate;
    private String deliveryAddress;
    private PaymentMethod paymentMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(BigDecimal deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getSecureId() {
        return secureId;
    }

    public void setSecureId(String secureId) {
        this.secureId = secureId;
    }
}
