package com.loan.evaluation.service.impl;

import com.loan.evaluation.dto.LoanApplicationRequest;
import com.loan.evaluation.dto.LoanResponse;
import com.loan.evaluation.entity.LoanApplication;
import com.loan.evaluation.repository.LoanApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanApplicationRepository repository;

    @InjectMocks
    private LoanServiceImpl loanService;

    // ----------- APPROVED CASE -----------
    @Test
    void shouldApproveLoanApplication() {

        LoanApplicationRequest request = buildValidRequest();

        when(repository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse response = loanService.processApplication(request);

        assertNotNull(response);
        assertEquals("APPROVED", response.getStatus());
        assertNotNull(response.getOffer());

        verify(repository, times(1)).save(any(LoanApplication.class));
    }

    // ----------- REJECT: LOW CREDIT SCORE -----------
    @Test
    void shouldRejectWhenCreditScoreLow() {

        LoanApplicationRequest request = buildValidRequest();
        request.getApplicant().setCreditScore(500);

        LoanResponse response = loanService.processApplication(request);

        assertEquals("REJECTED", response.getStatus());
        assertTrue(response.getRejectionReasons().contains("LOW_CREDIT_SCORE"));

        verify(repository, times(1)).save(any());
    }

    // ----------- REJECT: EMI TOO HIGH -----------
    @Test
    void shouldRejectWhenEmiExceedsIncome() {

        LoanApplicationRequest request = buildValidRequest();
        request.getApplicant().setMonthlyIncome(BigDecimal.valueOf(10000));
        request.getLoan().setAmount(BigDecimal.valueOf(1000000));

        LoanResponse response = loanService.processApplication(request);

        assertEquals("REJECTED", response.getStatus());
        assertTrue(response.getRejectionReasons().contains("EMI_EXCEEDS_60_PERCENT"));
    }

    @Test
    void shouldRejectWhenAgePlusTenureExceedsLimit() {

        LoanApplicationRequest request = buildValidRequest();

        // Set age high
        request.getApplicant().setAge(60);

        // Set long tenure (in months → converted to years)
        request.getLoan().setTenureMonths(120); // 10 years

        LoanResponse response = loanService.processApplication(request);

        assertEquals("REJECTED", response.getStatus());
        assertTrue(response.getRejectionReasons()
                .contains("AGE_TENURE_LIMIT_EXCEEDED"));
    }

    // ----------- HELPER METHOD -----------
    private LoanApplicationRequest buildValidRequest() {

        LoanApplicationRequest request = new LoanApplicationRequest();

        LoanApplicationRequest.Applicant applicant = new LoanApplicationRequest.Applicant();
        applicant.setName("Test User");
        applicant.setAge(30);
        applicant.setMonthlyIncome(BigDecimal.valueOf(80000));
        applicant.setEmploymentType(
                com.loan.evaluation.enums.EmploymentType.SALARIED);
        applicant.setCreditScore(750);

        LoanApplicationRequest.Loan loan = new LoanApplicationRequest.Loan();
        loan.setAmount(BigDecimal.valueOf(300000));
        loan.setTenureMonths(24);
        loan.setPurpose("PERSONAL");

        request.setApplicant(applicant);
        request.setLoan(loan);

        return request;
    }
}