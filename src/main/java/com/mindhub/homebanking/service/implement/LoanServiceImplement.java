package com.mindhub.homebanking.service.implement;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class LoanServiceImplement implements LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Override
    public List<LoanDTO> getLoansDTO() {
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(toList());
    }

    @Override
    public Loan findById(long id) {
        return loanRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Loan newLoan) {
        loanRepository.save(newLoan);
    }

    @Override
    public Loan findByName(String name) {
        return loanRepository.findByName(name);
    }

}
