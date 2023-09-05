package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.ClientService;
import com.mindhub.homebanking.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api")
public class LoanControllers {
    @Autowired
    private ClientService clientService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/loans")
    public List<LoanDTO> getLoansDTO() {
        return loanService.getLoansDTO();
    }
    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(@RequestBody LoanApplicationDTO loanAppDTO, Authentication authentication) {

        Client clientAuth = clientService.findByEmail(authentication.getName());

        if (clientAuth == null) {
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }

        LoanDTO loan = loanService.findById(loanAppDTO.getId());

        if (loan == null) {
            return new ResponseEntity<>("Loan not found", HttpStatus.FORBIDDEN);
        }
        if (loanAppDTO.getNumberAccountDestination().isBlank()) {
            return new ResponseEntity<>("Account destination not found", HttpStatus.FORBIDDEN);
        }
        if (loanAppDTO.getNumberAccountDestination() == null) {
            return new ResponseEntity<>("Account destination not found", HttpStatus.FORBIDDEN);
        }

        Account accountAuth = accountService.findByNumber(loanAppDTO.getNumberAccountDestination());

        if (clientAuth.getAccounts().contains(accountAuth)) {
            return new ResponseEntity<>("Source and destination accounts can't be the same", HttpStatus.FORBIDDEN);
        }
        if (loanAppDTO.getAmount() <= 0) {
            return new ResponseEntity<>("Amount must be greater than 0", HttpStatus.FORBIDDEN);
        }
        if (loanAppDTO.getPayments() == null || loanAppDTO.getPayments().size() <= 0) {
            return new ResponseEntity<>("Submit a description", HttpStatus.FORBIDDEN);
        }
        if(loanAppDTO.getAmount() > loan.getMaxAmount()){
            return new ResponseEntity<>("Amount must be less than or equal to " + loan.getMaxAmount(), HttpStatus.FORBIDDEN);
        }
        if (!loanAppDTO.getPayments().containsAll(loanAppDTO.getPayments())) {
            return new ResponseEntity<>("Submit a description", HttpStatus.FORBIDDEN);
        }

        double totalAmount = loanAppDTO.getAmount() * 0.2;
        Loan newLoan = new Loan(loan.getName(), totalAmount, loanAppDTO.getPayments());
        Transaction newTransaction = new Transaction(loanAppDTO.getAmount(), "", LocalDateTime.now(), TransactionType.CREDIT);
        accountAuth.addTransaction(newTransaction);
        transactionRepository.save(newTransaction);
        return null;
    }
}
