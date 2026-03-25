package com.loan.evaluation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LoanResponse {

    private UUID applicationId;
    private String status;
    private String riskBand;
    private Offer offer;
    private List<String> rejectionReasons;

    @Data
    @Builder
    public static class Offer {
        private BigDecimal interestRate;
        private int tenureMonths;
        private BigDecimal emi;
        private BigDecimal totalPayable;
    }
}