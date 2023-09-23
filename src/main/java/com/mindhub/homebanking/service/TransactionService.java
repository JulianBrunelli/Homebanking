package com.mindhub.homebanking.service;


import com.mindhub.homebanking.models.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface TransactionService {

    void saveTransaction(Transaction transaction);

    void saveTransactions(Set<Transaction> transactions);

    List<Transaction> findByDateBetweenAndAccountNumber(LocalDateTime startDate, LocalDateTime endDate, String accountNumber);
}
