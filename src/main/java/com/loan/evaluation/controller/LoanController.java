package com.loan.evaluation.controller;

import com.loan.evaluation.dto.LoanApplicationRequest;
import com.loan.evaluation.dto.LoanResponse;
import com.loan.evaluation.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> createApplication(
            @Valid @RequestBody LoanApplicationRequest request) {

        LoanResponse response = loanService.processApplication(request);
        return ResponseEntity.ok(response);
    }
}