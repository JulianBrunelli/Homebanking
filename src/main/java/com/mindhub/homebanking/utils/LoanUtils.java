package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public final class LoanUtils {
    public static Double calculateInterest(Loan loan, LoanApplicationDTO loanAppDTO) {
        int payment = loanAppDTO.getPayments();

        double initialInterest = loan.getInterest();
        double interest = 0;

        for (int i = 0; i < payment; i++) {
                interest = initialInterest;
                initialInterest += 0.5;
        }
        return interest;
    }
}
