package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.CardService;
import com.mindhub.homebanking.service.ClientService;
import com.mindhub.homebanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "/api")
public class ControllerCross {
    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    @Transactional
    @PostMapping("/transactions/cards")
    public ResponseEntity<Object> createCardsTransaction(@RequestBody CardTransactionDTO cardTransactionDTO) {

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

        Client client = card.getClient();
        Account account = client.getAccounts().stream().filter(account1 -> account1.getBalance() >= cardTransactionDTO.getAmount()).findFirst().orElse(null);

        if(account == null){
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

        Transaction transaction = new Transaction(cardTransactionDTO.getAmount() * -1, cardTransactionDTO.getDescription(), LocalDateTime.now(),TransactionType.DEBIT, account.getBalance() - cardTransactionDTO.getAmount());
        account.addTransaction(transaction);
        transactionService.saveTransaction(transaction);
        account.setBalance(account.getBalance() - cardTransactionDTO.getAmount());
        accountService.saveAccount(account);
        return new ResponseEntity<>("Transaction created", HttpStatus.CREATED);
    }

}

