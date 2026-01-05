package com.itwizard.payme.domain;

import com.itwizard.payme.domain.enums.LoanApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct product;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal maxEligibleAmount;

    @Column(nullable = false)
    private Integer tenorMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LoanApplicationStatus status;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
