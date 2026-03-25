package com.loan.evaluation.service;

import com.loan.evaluation.dto.LoanApplicationRequest;
import com.loan.evaluation.dto.LoanResponse;

public interface LoanService {

    LoanResponse processApplication(LoanApplicationRequest request);
}