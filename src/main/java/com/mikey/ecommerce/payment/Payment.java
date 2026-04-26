package com.mikey.ecommerce.payment;

import com.mikey.ecommerce.order.CustomerOrder;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private CustomerOrder order;

    @Column(nullable = false)
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant paidAt = Instant.now();

    protected Payment() {}

    public Payment(CustomerOrder order, String paymentMethod, PaymentStatus status, BigDecimal amount) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public CustomerOrder getOrder() { return order; }
    public String getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public Instant getPaidAt() { return paidAt; }
}
