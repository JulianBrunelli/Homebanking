package com.mindhub.homebanking.service.implement;

import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class TransactionServiceImplement implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public void saveTransactions(Set<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
    }

    @Override
    public List<Transaction> findByDateBetweenAndAccountNumber(LocalDate startDate, LocalDate endDate, String accountNumber) {
        return transactionRepository.findByDateBetweenAndAccountNumber(startDate, endDate, accountNumber);
    }

}
