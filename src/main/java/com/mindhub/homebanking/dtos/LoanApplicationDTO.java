package com.mindhub.homebanking.dtos;

import java.util.ArrayList;
import java.util.List;

public class LoanApplicationDTO {
    private long id;
    private double amount;
    private List<Integer> payments;
    private String numberAccountDestination;

    public LoanApplicationDTO() {
    }
    public LoanApplicationDTO(long id, double amount, List<Integer> payments, String numberAccountDestination) {
        this.id = id;
        this.amount = amount;
        this.payments = payments;
        this.numberAccountDestination = numberAccountDestination;
    }
    public long getId() {
        return id;
    }
    public double getAmount() {
        return amount;
    }
    public List<Integer> getPayments() {
        return payments;
    }
    public String getNumberAccountDestination() {
        return numberAccountDestination;
    }
}

