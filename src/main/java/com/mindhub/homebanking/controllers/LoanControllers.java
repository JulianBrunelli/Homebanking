package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.mindhub.homebanking.utils.LoanUtils.calculateInterest;

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
    private TransactionService transactionService;
    @Autowired
    private ClientLoanService clientLoanService;

    @GetMapping("/loans")
    public List<LoanDTO> getLoansDTO() {
        return loanService.getLoansDTO();
    }
    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(@RequestBody LoanApplicationDTO loanAppDTO, Authentication authentication) {

        String userName = authentication.getName();
        Client clientAuth = clientService.findByEmail(userName);

        if (clientAuth == null) {
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }

        Loan loan = loanService.findById(loanAppDTO.getId());

        if (loan == null) {
            return new ResponseEntity<>("Loan not found", HttpStatus.FORBIDDEN);
        }
        if(loanAppDTO.getAmount() <= 0){
            return new ResponseEntity<>("You cannot enter an amount less than or equal to 0", HttpStatus.FORBIDDEN);
        }
        if (loanAppDTO.getPayments() == null) {
            return new ResponseEntity<>("The selected quota is not valid", HttpStatus.FORBIDDEN);
        }
        if(loanAppDTO.getAmount() > loan.getMaxAmount()){
            return new ResponseEntity<>("Amount must be less than or equal to " + loan.getMaxAmount(), HttpStatus.FORBIDDEN);
        }
        if (!loan.getPayments().contains(loanAppDTO.getPayments())) {
            return new ResponseEntity<>("You cannot have two loans of the same type", HttpStatus.FORBIDDEN);
        }

        Account account = accountService.findByNumber(loanAppDTO.getNumberAccountDestination());

        if(loanAppDTO.getNumberAccountDestination().isBlank()){
            return new ResponseEntity<>("Please enter an account number", HttpStatus.FORBIDDEN);
        }
        if (account == null) {
            return new ResponseEntity<>("No similar account was found in our records.", HttpStatus.FORBIDDEN);
        }
        if(!clientAuth.getAccounts().contains(account)){
            return new ResponseEntity<>("The provided account does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }
        if (clientLoanService.existsByClientAndLoan(clientAuth, loan)) {
            return new ResponseEntity<>("You cannot have two loans of the same type", HttpStatus.FORBIDDEN);
        }

        double interest = calculateInterest(loan, loanAppDTO);

        double totalAmount = loanAppDTO.getAmount() + (loanAppDTO.getAmount() * (interest / 100));
        ClientLoan clientLoan = new ClientLoan(totalAmount, loanAppDTO.getPayments());

        account.setBalance(account.getBalance() + loanAppDTO.getAmount());
        clientLoan.setClient(clientAuth);
        clientLoan.setLoan(loan);
        clientLoanService.save(clientLoan);

        Transaction newTransaction = new Transaction(loanAppDTO.getAmount(), loan.getName(), LocalDateTime.now(), TransactionType.CREDIT, account.getBalance());

        transactionService.saveTransaction(newTransaction);
        accountService.saveAccount(account);
        account.addTransaction(newTransaction);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/loans/create")
    public ResponseEntity<Object> createLoan(@RequestBody LoanDTO loanDTO, Authentication authentication) {

        Client clientAuth = clientService.findByEmail(authentication.getName());
        if (clientAuth == null) {
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }

        Loan loan = loanService.findByName(loanDTO.getName());
        if(loan != null){
            return new ResponseEntity<>("Loan already exists", HttpStatus.FORBIDDEN);
        }
        if(loanDTO.getName() == null || loanDTO.getName().isBlank()){
            return new ResponseEntity<>("Please enter a name", HttpStatus.FORBIDDEN);
        }
        if(loanDTO.getMaxAmount() <= 1000){
            return new ResponseEntity<>("You cannot enter an amount less than or equal to 1000", HttpStatus.FORBIDDEN);
        }
        if (loanDTO.getPayments() == null || loanDTO.getPayments().toString().isBlank()) {
            return new ResponseEntity<>("The selected quota is not valid", HttpStatus.FORBIDDEN);
        }
        if(loanDTO.getInterest() <= 0){
            return new ResponseEntity<>("You must select an initial interest", HttpStatus.FORBIDDEN);
        }
        Loan newLoan = new Loan(loanDTO.getName(), loanDTO.getMaxAmount(), loanDTO.getPayments(), loanDTO.getInterest());
        loanService.save(newLoan);

        return new ResponseEntity<>("Loan created successfully",HttpStatus.CREATED);
    }
}
