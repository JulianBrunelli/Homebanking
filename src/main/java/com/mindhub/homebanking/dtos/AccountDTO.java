package com.mindhub.homebanking.dtos;

import java.time.LocalDateTime;

public class AccountDTO {
    private long id;
    private String number;
    private LocalDateTime creationDate;
    private double balance;

    public AccountDTO(){
    }
    public AccountDTO(long id, String number, LocalDateTime creationDate, double balance) {
        this.id = id;
        this.number = number;
        this.creationDate = creationDate;
        this.balance = balance;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
}

