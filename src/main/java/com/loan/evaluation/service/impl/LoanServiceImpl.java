package com.loan.evaluation.service.impl;

import com.loan.evaluation.dto.LoanApplicationRequest;
import com.loan.evaluation.dto.LoanResponse;
import com.loan.evaluation.enums.EmploymentType;
import com.loan.evaluation.enums.RiskBand;
import com.loan.evaluation.service.LoanService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class LoanServiceImpl implements LoanService {

    private static final BigDecimal BASE_RATE = BigDecimal.valueOf(12);

    @Override
    public LoanResponse processApplication(LoanApplicationRequest request) {

        LoanApplicationRequest.Applicant applicant = request.getApplicant();
        LoanApplicationRequest.Loan loan = request.getLoan();

        List<String> rejectionReasons = new ArrayList<>();

        // 1. Risk Band
        RiskBand riskBand = getRiskBand(applicant.getCreditScore());

        // 2. Interest Rate
        BigDecimal interestRate = calculateInterestRate(riskBand, applicant, loan);

        // 3. EMI
        BigDecimal emi = calculateEMI(loan.getAmount(), interestRate, loan.getTenureMonths());

        // 4. Eligibility Checks

        if (applicant.getCreditScore() < 600) {
            rejectionReasons.add("LOW_CREDIT_SCORE");
        }

        int ageWithTenure = applicant.getAge() + (loan.getTenureMonths() / 12);
        if (ageWithTenure > 65) {
            rejectionReasons.add("AGE_TENURE_LIMIT_EXCEEDED");
        }

        BigDecimal sixtyPercentIncome = applicant.getMonthlyIncome().multiply(BigDecimal.valueOf(0.6));
        if (emi.compareTo(sixtyPercentIncome) > 0) {
            rejectionReasons.add("EMI_EXCEEDS_60_PERCENT");
        }

        BigDecimal fiftyPercentIncome = applicant.getMonthlyIncome().multiply(BigDecimal.valueOf(0.5));
        if (emi.compareTo(fiftyPercentIncome) > 0) {
            rejectionReasons.add("EMI_EXCEEDS_50_PERCENT");
        }

        UUID applicationId = UUID.randomUUID();

        // 5. Reject Case
        if (!rejectionReasons.isEmpty()) {
            return LoanResponse.builder()
                    .applicationId(applicationId)
                    .status("REJECTED")
                    .rejectionReasons(rejectionReasons)
                    .build();
        }

        // 6. Approve Case
        BigDecimal totalPayable = emi.multiply(BigDecimal.valueOf(loan.getTenureMonths()));

        return LoanResponse.builder()
                .applicationId(applicationId)
                .status("APPROVED")
                .riskBand(riskBand.name())
                .offer(LoanResponse.Offer.builder()
                        .interestRate(interestRate)
                        .tenureMonths(loan.getTenureMonths())
                        .emi(emi)
                        .totalPayable(totalPayable)
                        .build())
                .build();
    }

    // ---------------- HELPER METHODS ----------------

    private RiskBand getRiskBand(int score) {
        if (score >= 750) return RiskBand.LOW;
        if (score >= 650) return RiskBand.MEDIUM;
        return RiskBand.HIGH;
    }

    private BigDecimal calculateInterestRate(RiskBand riskBand,
                                             LoanApplicationRequest.Applicant applicant,
                                             LoanApplicationRequest.Loan loan) {

        BigDecimal rate = BASE_RATE;

        // Risk premium
        if (riskBand == RiskBand.MEDIUM) rate = rate.add(BigDecimal.valueOf(1.5));
        if (riskBand == RiskBand.HIGH) rate = rate.add(BigDecimal.valueOf(3));

        // Employment premium
        if (applicant.getEmploymentType() == EmploymentType.SELF_EMPLOYED) {
            rate = rate.add(BigDecimal.valueOf(1));
        }

        // Loan size premium
        if (loan.getAmount().compareTo(BigDecimal.valueOf(1000000)) > 0) {
            rate = rate.add(BigDecimal.valueOf(0.5));
        }

        return rate;
    }

    private BigDecimal calculateEMI(BigDecimal principal,
                                    BigDecimal annualRate,
                                    int tenureMonths) {

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        BigDecimal onePlusRPowerN = (monthlyRate.add(BigDecimal.ONE)).pow(tenureMonths);

        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);

        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}