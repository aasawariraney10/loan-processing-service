package com.loan.evaluation.dto;

import com.loan.evaluation.enums.EmploymentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanApplicationRequest {

    @Valid
    @NotNull
    private Applicant applicant;

    @Valid
    @NotNull
    private Loan loan;

    @Data
    public static class Applicant {

        @NotBlank(message = "Applicant name must not be empty")
        private String name;

        @Min(value = 21, message = "Age must be at least 21")
        @Max(value = 60, message = "Age must not exceed 60")
        private int age;

        @NotNull(message = "Monthly income is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be greater than 0")
        private BigDecimal monthlyIncome;

        @NotNull(message = "Employment type is required")
        private EmploymentType employmentType;

        @Min(value = 300, message = "Credit score must be at least 300")
        @Max(value = 900, message = "Credit score must not exceed 900")
        private int creditScore;
    }

    @Data
    public static class Loan {

        @NotNull(message = "Loan amount is required")
        @DecimalMin(value = "10000", message = "Loan amount must be at least 10,000")
        @DecimalMax(value = "5000000", message = "Loan amount must not exceed 50,00,000")
        private BigDecimal amount;

        @Min(value = 6, message = "Tenure must be at least 6 months")
        @Max(value = 360, message = "Tenure must not exceed 360 months")
        private int tenureMonths;

        @NotBlank(message = "Loan purpose is required")
        private String purpose;
    }
}