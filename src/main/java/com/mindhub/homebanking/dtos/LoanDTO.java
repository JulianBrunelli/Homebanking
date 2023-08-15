package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoanDTO {
    private long id;
    private String name;
    private double maxAmount;
    private List<Integer> payments = new ArrayList<>();
    private Set<ClientLoan> clientLoans = new HashSet<>();
    public LoanDTO() {
    }

    public LoanDTO(long id, String name, double maxAmount, List<Integer> payments, Set<ClientLoan> clientLoans) {
        this.id = id;
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
        this.clientLoans = clientLoans;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public Set<ClientLoan> getClientLoans() {
        return clientLoans;
    }

}

