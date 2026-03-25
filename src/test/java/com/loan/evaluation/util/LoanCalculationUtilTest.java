package com.loan.evaluation.util;

import com.loan.evaluation.dto.LoanApplicationRequest;
import com.loan.evaluation.enums.EmploymentType;
import com.loan.evaluation.enums.RiskBand;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanCalculationUtilTest {

    // ----------- RISK BAND TESTS -----------

    @Test
    void shouldReturnLowRiskBand() {
        assertEquals(RiskBand.LOW, LoanCalculationUtil.getRiskBand(750));
        assertEquals(RiskBand.LOW, LoanCalculationUtil.getRiskBand(800));
    }

    @Test
    void shouldReturnMediumRiskBand() {
        assertEquals(RiskBand.MEDIUM, LoanCalculationUtil.getRiskBand(700));
    }

    @Test
    void shouldReturnHighRiskBand() {
        assertEquals(RiskBand.HIGH, LoanCalculationUtil.getRiskBand(600));
    }

    // ----------- INTEREST RATE TESTS -----------

    @Test
    void shouldCalculateInterestRateForLowRiskSalaried() {

        LoanApplicationRequest.Applicant applicant = new LoanApplicationRequest.Applicant();
        applicant.setEmploymentType(EmploymentType.SALARIED);

        LoanApplicationRequest.Loan loan = new LoanApplicationRequest.Loan();
        loan.setAmount(BigDecimal.valueOf(500000));

        BigDecimal rate = LoanCalculationUtil.calculateInterestRate(
                RiskBand.LOW, applicant, loan);

        assertEquals(BigDecimal.valueOf(12), rate);
    }

    @Test
    void shouldCalculateInterestRateForHighRiskSelfEmployedAndHighLoan() {

        LoanApplicationRequest.Applicant applicant = new LoanApplicationRequest.Applicant();
        applicant.setEmploymentType(EmploymentType.SELF_EMPLOYED);

        LoanApplicationRequest.Loan loan = new LoanApplicationRequest.Loan();
        loan.setAmount(BigDecimal.valueOf(1500000)); // > 10L

        BigDecimal rate = LoanCalculationUtil.calculateInterestRate(
                RiskBand.HIGH, applicant, loan);

        // 12 + 3 (high risk) + 1 (self-employed) + 0.5 (loan size)
        assertEquals(BigDecimal.valueOf(16.5), rate);
    }

    // ----------- EMI TESTS -----------

    @Test
    void shouldCalculateEmiCorrectly() {

        BigDecimal principal = BigDecimal.valueOf(100000);
        BigDecimal annualRate = BigDecimal.valueOf(12);
        int tenureMonths = 12;

        BigDecimal emi = LoanCalculationUtil.calculateEMI(
                principal, annualRate, tenureMonths);

        assertNotNull(emi);
        assertTrue(emi.compareTo(BigDecimal.ZERO) > 0);

        // Optional: precise check (approx)
        assertEquals(8884.88, emi.doubleValue(), 0.1);
    }

    @Test
    void shouldHandleZeroOrSmallValuesGracefully() {

        BigDecimal principal = BigDecimal.valueOf(1000);
        BigDecimal annualRate = BigDecimal.valueOf(10);
        int tenureMonths = 6;

        BigDecimal emi = LoanCalculationUtil.calculateEMI(
                principal, annualRate, tenureMonths);

        assertNotNull(emi);
    }
}