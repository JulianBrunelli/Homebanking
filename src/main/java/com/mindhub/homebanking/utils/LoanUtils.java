package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.models.Loan;

public final class LoanUtils {
    public static Double calculateInterest(Loan loan, LoanApplicationDTO loanAppDTO) {

        double initialInterest = loan.getInterest();
        double interest = 0;

        do{
            interest = initialInterest;
            initialInterest += 5;
        }while (loan.getPayments().equals(loanAppDTO.getPayments()));

        return interest;
    }
}
