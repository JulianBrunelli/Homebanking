package com.mindhub.homebanking.service;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    List<LoanDTO> getLoansDTO();
    Loan findById(long id);
    void save(Loan newLoan);

    Loan findByName(String name);
}
