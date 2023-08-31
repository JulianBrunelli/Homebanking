package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountControllers {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;
    private String randomNumber(){
        String random;
        do {
            int number = (int) (Math.random()*1000 + 9999);
            random = "VIN-" + number;
        }while (accountRepository.findByNumber(random)!=null);
        return random;
    }
    @RequestMapping("/accounts")
    public List<AccountDTO>getAccount(){
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toList());
    }
    @RequestMapping("/clients/accounts/{id}")
    public ResponseEntity<Object> getAccount(@PathVariable Long id, Authentication authentication){
        if (clientRepository.findByEmail(authentication.getName()).getAccounts() != null){
            Client client = clientRepository.findByEmail(authentication.getName());
            Account account = client.getAccounts().stream().filter(account1 -> account1.getId() == id).findFirst().orElse(null);
            return new ResponseEntity<>(new AccountDTO(account), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping( path = "/clients/current/accounts")
    public ResponseEntity<Object>newAccount(Authentication authentication){
        if (clientRepository.findByEmail(authentication.getName()).getAccounts().size() <= 2){
            String accountNumber = randomNumber();
            Account account = new Account(accountNumber, LocalDate.now(),0.0);
            clientRepository.findByEmail(authentication.getName()).addAccount(account);
            accountRepository.save(account);
        }else{
            return new ResponseEntity<>("Failed to create account because the maximum number of accounts is 3", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Your account was successfully created", HttpStatus.CREATED);
    }
}
