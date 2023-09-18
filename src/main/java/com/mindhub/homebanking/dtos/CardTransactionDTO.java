package com.mindhub.homebanking.dtos;

public class CardTransactionDTO {
    private Long id;
    private String number;
    private int cvv;
    private double amount;
    private String description;

    public CardTransactionDTO() {
    }

    public CardTransactionDTO(Long id, String number, int cvv, double amount, String description) {
        this.id = id;
        this.number = number;
        this.cvv = cvv;
        this.amount = amount;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public int getCvv() {
        return cvv;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
