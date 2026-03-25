package com.loan.evaluation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.evaluation.dto.LoanApplicationRequest;
import com.loan.evaluation.dto.LoanResponse;
import com.loan.evaluation.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    // ----------- SUCCESS CASE -----------
    @Test
    void shouldCreateLoanApplicationSuccessfully() throws Exception {

        LoanApplicationRequest request = buildValidRequest();

        LoanResponse response = LoanResponse.builder()
                .applicationId(UUID.randomUUID())
                .status("APPROVED")
                .riskBand("LOW")
                .build();

        when(loanService.processApplication(request)).thenReturn(response);

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    // ----------- VALIDATION ERROR CASE -----------
    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {

        LoanApplicationRequest request = new LoanApplicationRequest(); // empty

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
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