package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = "/api")
public class ClientControllers {
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private String randomNumber(){
        String random;
        do {
            int number = (int) (Math.random()*1000 + 9999);
            random = "VIN-" + number;
        }while (accountService.findByNumber(random)!=null);
        return random;
    }
    @PostMapping(path = "/clients")
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isBlank()) {
            return new ResponseEntity<>("Please provide a first name", HttpStatus.FORBIDDEN);
        }
        if(lastName.isBlank()){
            return new ResponseEntity<>("Please provide a last name", HttpStatus.FORBIDDEN);
        }
        if(email.isBlank() || !email.contains("@") || !email.contains(".com")){
            return new ResponseEntity<>("Please enter an email containing an @ and .com", HttpStatus.FORBIDDEN);
        }
        if(password.isBlank()){
            return new ResponseEntity<>("Please provide a password", HttpStatus.FORBIDDEN);
        }
        if (clientService.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);
        }

        Client newClient = new Client(firstName, lastName,email.toLowerCase(), passwordEncoder.encode(password));
        clientService.save(newClient);
        String number = randomNumber();
        Account account = new Account(number, LocalDate.now(),0.0);
        newClient.addAccount(account);
        accountService.saveAccount(account);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/clients/current")
    public ClientDTO getClient(Authentication authentication) {
        return clientService.getClientDTO(authentication.getName());
    }
    @GetMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientService.getClientsDTO();
    }
    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientService.findById(id);
    }
}
