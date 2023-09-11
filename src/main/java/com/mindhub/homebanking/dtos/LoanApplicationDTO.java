package com.mindhub.homebanking.dtos;

public class LoanApplicationDTO {
    private long id;
    private double amount;
    private Integer payments;
    private String numberAccountDestination;

    public LoanApplicationDTO() {
    }
    public LoanApplicationDTO(long id, double amount, Integer payments, String numberAccountDestination) {
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
    public Integer getPayments() {
        return payments;
    }
    public String getNumberAccountDestination() {
        return numberAccountDestination;
    }
}

