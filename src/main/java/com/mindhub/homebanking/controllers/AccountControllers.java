package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.mindhub.homebanking.utils.CardUtils.getAccountNumber;

@RestController
@RequestMapping(path = "/api")
public class AccountControllers {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    private String randomNumber(){
        String random;
        do {
            random = getAccountNumber();
        }while (accountService.findByNumber(random)!=null);
        return random;
    }

    @GetMapping("/accounts")
    public List<AccountDTO>getAccount(){
        return accountService.getAccountsDTO();
    }
    @GetMapping("/clients/accounts/{id}")
    public ResponseEntity<Object> getAccount(@PathVariable Long id, Authentication authentication){
        if (clientService.findByEmail(authentication.getName()).getAccounts() != null){
            Client client = clientService.findByEmail(authentication.getName());
            Account account = client.getAccounts().stream().filter(account1 -> account1.getId() == id).findFirst().orElse(null);
            return new ResponseEntity<>(new AccountDTO(account), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object>newAccount(Authentication authentication){
        if (clientService.findByEmail(authentication.getName()).getAccounts().size() <= 2){
            String accountNumber = randomNumber();
            Account account = new Account(accountNumber, LocalDate.now(),0.0);
            clientService.findByEmail(authentication.getName()).addAccount(account);
            accountService.saveAccount(account);
        }else{
            return new ResponseEntity<>("Failed to create account because the maximum number of accounts is 3", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Your account was successfully created", HttpStatus.CREATED);
    }
}
