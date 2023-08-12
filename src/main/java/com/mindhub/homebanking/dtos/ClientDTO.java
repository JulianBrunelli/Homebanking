package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class ClientDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<AccountDTO> accounts = new HashSet<>();

    private Set<ClientLoan> loans;

    public ClientDTO() {
    }
    public ClientDTO(Client client) {
        this.id= client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.accounts = client.getAccounts()
                .stream()
                .map(account -> new AccountDTO(account))
                .collect(toSet());
        this.loans = client.getClientLoans();
    }

    public ClientDTO(Account account) {
    }
    public long getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Set<AccountDTO> getAccounts() {
        return accounts;
    }
    public void setAccounts(Set<AccountDTO> accounts) {
        this.accounts = accounts;
    }
    public Set<ClientLoan> getLoans() {
        return loans;
    }
    public void setLoans(Set<ClientLoan> loans) {
        this.loans = loans;
    }
}
