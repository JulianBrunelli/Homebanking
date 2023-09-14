package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.ClientService;
import com.mindhub.homebanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/api")
public class TransactionController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(
            Authentication authentication, @RequestParam double amount, @RequestParam String description,
            @RequestParam String originAccountNumber, @RequestParam String destinationAccountNumber){

        Client client = clientService.findByEmail(authentication.getName());
        Account originAccount = client.getAccounts().stream().filter(account -> account.getNumber().equals(originAccountNumber)).findFirst().orElse(null);
        Account destinationAccount = accountService.findByNumber(destinationAccountNumber);

        if (client == null){
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }
        if(originAccount == null){
            return new ResponseEntity<>("Origin account not found", HttpStatus.FORBIDDEN);
        }
        if(destinationAccount == null){
            return new ResponseEntity<>("Destination account not found", HttpStatus.FORBIDDEN);
        }
        if (originAccount.getNumber().equals(destinationAccount.getNumber())){
            return new ResponseEntity<>("Source and destination accounts can't be the same", HttpStatus.FORBIDDEN);
        }
        if(amount <= 0){
            return new ResponseEntity<>("Please enter an amount greater than 0", HttpStatus.FORBIDDEN);
        }
        if(description.isBlank()){
            return new ResponseEntity<>("Please enter a description", HttpStatus.FORBIDDEN);
        }
        if(description.length() > 15){
            return new ResponseEntity<>("Description can't be longer than 15 characters", HttpStatus.FORBIDDEN);

        }
        if (originAccount.getBalance() < amount){
            return new ResponseEntity<>("Not enough money in the account", HttpStatus.FORBIDDEN);
        } else {
            originAccount.setBalance(originAccount.getBalance() - amount);
            destinationAccount.setBalance(destinationAccount.getBalance() + amount);
            Transaction transactionDebit = new Transaction(amount * - 1, description, LocalDateTime.now(), TransactionType.DEBIT,originAccount.getBalance());
            Transaction transactionCredit = new Transaction(amount, description, LocalDateTime.now(), TransactionType.CREDIT,destinationAccount.getBalance());
            originAccount.addTransaction(transactionDebit);
            destinationAccount.addTransaction(transactionCredit);
            transactionService.saveTransaction(transactionDebit);
            transactionService.saveTransaction(transactionCredit);
            return new ResponseEntity<>("Transaction created", HttpStatus.CREATED);
        }
    }
}