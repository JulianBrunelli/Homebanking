package com.mindhub.homebanking.service;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;

public interface ClientLoanService {
    boolean existsByClientAndLoan(Client client, Loan loan);

    void save(ClientLoan clientLoan);

}
