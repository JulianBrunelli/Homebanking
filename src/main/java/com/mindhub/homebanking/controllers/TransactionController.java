package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.CardService;
import com.mindhub.homebanking.service.ClientService;
import com.mindhub.homebanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api")
public class TransactionController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CardService cardService;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(
            Authentication authentication, @RequestParam double amount, @RequestParam String description,
            @RequestParam String originAccountNumber, @RequestParam String destinationAccountNumber) {

        Client client = clientService.findByEmail(authentication.getName());
        if (client == null) {
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }

        Account originAccount = client.getAccounts().stream().filter(account -> account.getNumber().equals(originAccountNumber)).findFirst().orElse(null);
        if (originAccount == null) {
            return new ResponseEntity<>("Origin account not found", HttpStatus.FORBIDDEN);
        }

        Account destinationAccount = accountService.findByNumber(destinationAccountNumber);
        if (destinationAccount == null) {
            return new ResponseEntity<>("Destination account not found", HttpStatus.FORBIDDEN);
        }
        if (originAccount.getNumber().equals(destinationAccount.getNumber())) {
            return new ResponseEntity<>("Source and destination accounts can't be the same", HttpStatus.FORBIDDEN);
        }
        if (amount <= 0) {
            return new ResponseEntity<>("Please enter an amount greater than 0", HttpStatus.FORBIDDEN);
        }
        if (description.isBlank()) {
            return new ResponseEntity<>("Please enter a description", HttpStatus.FORBIDDEN);
        }
        if (description.length() > 15) {
            return new ResponseEntity<>("Description can't be longer than 15 characters", HttpStatus.FORBIDDEN);
        }
        if (originAccount.getBalance() < amount) {
            return new ResponseEntity<>("Not enough money in the account", HttpStatus.FORBIDDEN);
        } else {
            originAccount.setBalance(originAccount.getBalance() - amount);
            destinationAccount.setBalance(destinationAccount.getBalance() + amount);
            Transaction transactionDebit = new Transaction(amount * -1, description, LocalDateTime.now(), TransactionType.DEBIT, originAccount.getBalance());
            Transaction transactionCredit = new Transaction(amount, description, LocalDateTime.now(), TransactionType.CREDIT, destinationAccount.getBalance());
            originAccount.addTransaction(transactionDebit);
            destinationAccount.addTransaction(transactionCredit);
            transactionService.saveTransaction(transactionDebit);
            transactionService.saveTransaction(transactionCredit);
            return new ResponseEntity<>("Transaction created", HttpStatus.CREATED);
        }
    }

    @Transactional
    @PostMapping("/transactions/cards")
    public ResponseEntity<Object> createCardsTransaction(@RequestParam long id, @RequestBody CardTransactionDTO cardTransactionDTO) {

        Card card = cardService.findByNumber(cardTransactionDTO.getNumber());
        if(card == null){
            return new ResponseEntity<>("Card not found", HttpStatus.FORBIDDEN);
        }
        if(!card.isActive()){
            return new ResponseEntity<>("Card not active", HttpStatus.FORBIDDEN);
        }
        if(card.getFromDate().isBefore(ChronoLocalDate.from(LocalDateTime.now()))){
            return new ResponseEntity<>("Card is not active", HttpStatus.FORBIDDEN);
        }
        if(card.getCvv() != cardTransactionDTO.getCvv()){
            return new ResponseEntity<>("CVV does not match", HttpStatus.FORBIDDEN);
        }

        ClientDTO clientDTO = clientService.findById(id);
        Set<AccountDTO> account = clientDTO.getAccounts();
        AccountDTO accountSelect = account.stream().filter(account1 -> account1.getBalance() >= cardTransactionDTO.getAmount()).findFirst().orElse(null);
        Account accountPayment = new Account(accountSelect);

        if(accountSelect == null){
            return new ResponseEntity<>("Insufficient balance", HttpStatus.FORBIDDEN);
        }

        if(cardTransactionDTO.getAmount() <= 0){
            return new ResponseEntity<>("Please enter an amount greater than 0", HttpStatus.FORBIDDEN);
        }

        if(cardTransactionDTO.getDescription().isBlank()){
            return new ResponseEntity<>("Please enter a description", HttpStatus.FORBIDDEN);
        }
        if(cardTransactionDTO.getDescription().length() > 15){
            return new ResponseEntity<>("Description can't be longer than 15 characters", HttpStatus.FORBIDDEN);

        }

        Transaction transaction = new Transaction(cardTransactionDTO.getAmount(), cardTransactionDTO.getDescription(), LocalDateTime.now(), TransactionType.DEBIT, accountSelect.getBalance());
        accountPayment.addTransaction(transaction);
        accountPayment.setBalance(accountPayment.getBalance() - cardTransactionDTO.getAmount());
        transactionService.saveTransaction(transaction);
        accountService.saveAccount(accountPayment);
        return new ResponseEntity<>("Transaction created", HttpStatus.CREATED);
    }
}