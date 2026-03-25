package com.loan.evaluation.service.impl;

import com.loan.evaluation.dto.LoanApplicationRequest;
import com.loan.evaluation.dto.LoanResponse;
import com.loan.evaluation.entity.LoanApplication;
import com.loan.evaluation.enums.RiskBand;
import com.loan.evaluation.repository.LoanApplicationRepository;
import com.loan.evaluation.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.loan.evaluation.constants.AppConstants.*;
import static com.loan.evaluation.enums.Status.APPROVED;
import static com.loan.evaluation.enums.Status.REJECTED;
import static com.loan.evaluation.util.LoanCalculationUtil.*;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanApplicationRepository repository;

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
            rejectionReasons.add(LOW_CREDIT_SCORE);
        }

        int ageWithTenure = applicant.getAge() + (loan.getTenureMonths() / 12);
        if (ageWithTenure > 65) {
            rejectionReasons.add(AGE_TENURE_LIMIT_EXCEEDED);
        }

        BigDecimal sixtyPercentIncome = applicant.getMonthlyIncome().multiply(BigDecimal.valueOf(0.6));
        if (emi.compareTo(sixtyPercentIncome) > 0) {
            rejectionReasons.add(EMI_EXCEEDS_60_PERCENT);
        }

        BigDecimal fiftyPercentIncome = applicant.getMonthlyIncome().multiply(BigDecimal.valueOf(0.5));
        if (emi.compareTo(fiftyPercentIncome) > 0) {
            rejectionReasons.add(EMI_EXCEEDS_50_PERCENT);
        }

        UUID applicationId = UUID.randomUUID();

        // 5. Reject Case
        if (!rejectionReasons.isEmpty()) {

            LoanApplication entity = new LoanApplication();
            entity.setApplicationId(applicationId);
            entity.setStatus(REJECTED.getValue());
            entity.setRejectionReasons(String.join(",", rejectionReasons));

            repository.save(entity);

            return LoanResponse.builder()
                    .applicationId(applicationId)
                    .status(REJECTED.getValue())
                    .rejectionReasons(rejectionReasons)
                    .build();
        }

        // 6. Approve Case
        BigDecimal totalPayable = emi.multiply(BigDecimal.valueOf(loan.getTenureMonths()));

        LoanApplication entity = new LoanApplication();
        entity.setApplicationId(applicationId);
        entity.setStatus(APPROVED.getValue());
        entity.setRiskBand(riskBand.name());
        entity.setInterestRate(interestRate);
        entity.setEmi(emi);
        entity.setTotalPayable(totalPayable);

        repository.save(entity);

        return LoanResponse.builder()
                .applicationId(applicationId)
                .status(APPROVED.getValue())
                .riskBand(riskBand.name())
                .offer(LoanResponse.Offer.builder()
                        .interestRate(interestRate)
                        .tenureMonths(loan.getTenureMonths())
                        .emi(emi)
                        .totalPayable(totalPayable)
                        .build())
                .build();
    }

}