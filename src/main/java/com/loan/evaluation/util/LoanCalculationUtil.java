package com.loan.evaluation.util;

import com.loan.evaluation.dto.LoanApplicationRequest;
import com.loan.evaluation.enums.EmploymentType;
import com.loan.evaluation.enums.RiskBand;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LoanCalculationUtil {

    private static final BigDecimal BASE_RATE = BigDecimal.valueOf(12);

    private LoanCalculationUtil() {}

    public static RiskBand getRiskBand(int score) {
        if (score >= 750) return RiskBand.LOW;
        if (score >= 650) return RiskBand.MEDIUM;
        return RiskBand.HIGH;
    }

    public static BigDecimal calculateInterestRate(RiskBand riskBand,
                                                   LoanApplicationRequest.Applicant applicant,
                                                   LoanApplicationRequest.Loan loan) {

        BigDecimal rate = BASE_RATE;

        if (riskBand == RiskBand.MEDIUM) rate = rate.add(BigDecimal.valueOf(1.5));
        if (riskBand == RiskBand.HIGH) rate = rate.add(BigDecimal.valueOf(3));

        if (applicant.getEmploymentType() == EmploymentType.SELF_EMPLOYED) {
            rate = rate.add(BigDecimal.valueOf(1));
        }

        if (loan.getAmount().compareTo(BigDecimal.valueOf(1000000)) > 0) {
            rate = rate.add(BigDecimal.valueOf(0.5));
        }

        return rate;
    }

    public static BigDecimal calculateEMI(BigDecimal principal,
                                          BigDecimal annualRate,
                                          int tenureMonths) {

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        BigDecimal onePlusRPowerN = (monthlyRate.add(BigDecimal.ONE)).pow(tenureMonths);

        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);

        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}