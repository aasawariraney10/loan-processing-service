package com.loan.evaluation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "loan_application")
@Data
public class LoanApplication {

    @Id
    private UUID applicationId;

    private String status;

    private String riskBand;

    private BigDecimal interestRate;

    private BigDecimal emi;

    private BigDecimal totalPayable;

    @Column(columnDefinition = "TEXT")
    private String rejectionReasons;
}