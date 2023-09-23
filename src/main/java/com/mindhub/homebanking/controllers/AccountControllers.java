package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.ClientService;
import com.mindhub.homebanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.CardUtils.getAccountNumber;

@RestController
@RequestMapping(path = "/api")
public class AccountControllers {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;
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
        Client client = clientService.findByEmail(authentication.getName());
        if (client.getAccounts() != null){
            Account account = client.getAccounts().stream().filter(account1 -> account1.getId() == id).findFirst().orElse(null);
            return new ResponseEntity<>(new AccountDTO(account), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object>newAccount(@RequestParam String type, Authentication authentication){
        Client clientAuth = clientService.findByEmail(authentication.getName());
        if(clientAuth == null){
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }
        if(type == null || type.isBlank()){
            return new ResponseEntity<>("Type not found", HttpStatus.FORBIDDEN);
        }
        if( !type.equals("CURRENT") && !type.equals("SAVINGS") ){
            return new ResponseEntity<>("Select a valid type", HttpStatus.FORBIDDEN);
        }
        Set<Account> accountsActive = clientAuth.getAccounts().stream().filter(account -> account.isActive()).collect(Collectors.toSet());
        if(accountsActive.size() >= 3){
            return new ResponseEntity<>("Failed to create account because the maximum number of accounts is 3", HttpStatus.FORBIDDEN);
        }
        String accountNumber = randomNumber();
        AccountType accountType =  AccountType.valueOf(type);
        Account account = new Account(accountNumber, LocalDate.now(),0.0,true,accountType);
        clientAuth.addAccount(account);
        accountService.saveAccount(account);
        return new ResponseEntity<>("Your account was successfully created", HttpStatus.CREATED);
    }

    @PatchMapping("/clients/current/accounts/deactivate")
    public ResponseEntity<Object>disableAccount(@RequestParam long id, Authentication authentication){

        Client client = clientService.findByEmail(authentication.getName());
        if(client == null){
            return new ResponseEntity<>("Client not found", HttpStatus.FORBIDDEN);
        }

        Account account = client.getAccounts().stream().filter(account1 -> account1.getId() == id).findFirst().orElse(null);
        Set<Account> accounts = client.getAccounts();
        long accountActive = accounts.stream().filter(account1 -> account1.isActive()).count();

        if(accountActive <= 1){
            return new ResponseEntity<>("You cannot delete the only account you have.", HttpStatus.FORBIDDEN);
        }
        if(account == null){
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }
        if(!client.getAccounts().contains(account)){
            return new ResponseEntity<>("The letter does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }
        if(!account.isActive()){
            return new ResponseEntity<>("Account already disabled", HttpStatus.FORBIDDEN);
        }
        if(account.getBalance() != 0.0){
            return new ResponseEntity<>("The account you want to delete currently has money", HttpStatus.FORBIDDEN);
        }
        Set<Transaction> transactions = account.getTransaction();

        transactions.forEach(transaction -> transaction.setActive(false));
        transactionService.saveTransactions(transactions);
        account.setActive(false);
        accountService.saveAccount(account);

        return new ResponseEntity<>("Successfully deleted account",HttpStatus.OK);
    }
}