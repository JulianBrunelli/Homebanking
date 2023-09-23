package com.mindhub.homebanking.controllers;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.service.AccountService;
import com.mindhub.homebanking.service.CardService;
import com.mindhub.homebanking.service.ClientService;
import com.mindhub.homebanking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api")
public class TransactionController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CardService cardService;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(
            Authentication authentication, @RequestParam double amount, @RequestParam String description,
            @RequestParam String originAccountNumber, @RequestParam String destinationAccountNumber) {

        Client client = clientService.findByEmail(authentication.getName());

        Account originAccount = client.getAccounts().stream().filter(account -> account.getNumber().equals(originAccountNumber)).findFirst().orElse(null);
        if (originAccount == null) {
            return new ResponseEntity<>("Origin account not found", HttpStatus.FORBIDDEN);
        }

        Account destinationAccount = accountService.findByNumber(destinationAccountNumber);
        if (destinationAccount == null) {
            return new ResponseEntity<>("Destination account not found", HttpStatus.FORBIDDEN);
        }
        if (originAccount.getNumber().equals(destinationAccount.getNumber())) {
            return new ResponseEntity<>("Source and destination accounts can't be the same", HttpStatus.FORBIDDEN);
        }
        if (amount <= 0) {
            return new ResponseEntity<>("Please enter an amount greater than 0", HttpStatus.FORBIDDEN);
        }
        if (description.isBlank()) {
            return new ResponseEntity<>("Please enter a description", HttpStatus.FORBIDDEN);
        }
        if (description.length() > 15) {
            return new ResponseEntity<>("Description can't be longer than 15 characters", HttpStatus.FORBIDDEN);
        }
        if (originAccount.getBalance() < amount) {
            return new ResponseEntity<>("Not enough money in the account", HttpStatus.FORBIDDEN);
        } else {
            originAccount.setBalance(originAccount.getBalance() - amount);
            destinationAccount.setBalance(destinationAccount.getBalance() + amount);
            Transaction transactionDebit = new Transaction(amount * -1, description, LocalDateTime.now(), TransactionType.DEBIT, originAccount.getBalance());
            Transaction transactionCredit = new Transaction(amount, description, LocalDateTime.now(), TransactionType.CREDIT, destinationAccount.getBalance());
            originAccount.addTransaction(transactionDebit);
            destinationAccount.addTransaction(transactionCredit);
            transactionService.saveTransaction(transactionDebit);
            transactionService.saveTransaction(transactionCredit);
            return new ResponseEntity<>("Transaction created", HttpStatus.CREATED);
        }
    }

    @PostMapping("/transactions/pdf")
    public ResponseEntity<Object> createPdf(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String accountNumber,
                                            Authentication authentication) throws DocumentException, IOException {

        if (authentication == null) {
            return new ResponseEntity<>("Authentication not found", HttpStatus.FORBIDDEN);
        }
        Client client = clientService.findByEmail(authentication.getName());

        Account account = client.getAccounts().stream().filter(account1 -> account1.getNumber().equals(accountNumber)).findFirst().orElse(null);
        if (account == null) {
            return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
        }
        if (endDate == null) {
            return new ResponseEntity<>("Please enter an end date", HttpStatus.NOT_FOUND);
        }
        if (startDate == null) {
            return new ResponseEntity<>("Please enter a start date", HttpStatus.NOT_FOUND);

        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime startDateLocalDate = LocalDateTime.parse(startDate,formatter);
        LocalDateTime endDateLocalDate = LocalDateTime.parse(endDate,formatter);
        List<Transaction> transactions = transactionService.findByDateBetweenAndAccountNumber(startDateLocalDate, endDateLocalDate, accountNumber);
        if (transactions.isEmpty()) {
            return new ResponseEntity<>("Transactions not found", HttpStatus.NOT_FOUND);
        }
        com.lowagie.text.Document document = new Document() {
        };
        try {
            PdfWriter.getInstance(document, new FileOutputStream("Paragraphs.pdf"));
            document.open();
            Paragraph p1 = new Paragraph(new Chunk("Details Account Selected", FontFactory.getFont(FontFactory.HELVETICA, 10)));
            p1.add("The leading of this paragraph is calculated automagically. ");
            p1.add("The default leading is 1.5 times the fontsize. ");
            p1.add(new Chunk("You can add chunks "));
            p1.add(new Phrase("or you can add phrases. "));
            p1.add(new Phrase(
                    "Unless you change the leading with the method setLeading, the leading doesn't change if you add text with another leading. This can lead to some problems.",
                    FontFactory.getFont(FontFactory.HELVETICA, 18)));
            document.add(p1);
            Paragraph p2 = new Paragraph(new Phrase(
                    "This is my second paragraph. ", FontFactory.getFont(
                    FontFactory.HELVETICA, 12)));
            p2.add("As you can see, it started on a new line.");
            document.add(p2);
            Paragraph p3 = new Paragraph("This is my third paragraph.",
                    FontFactory.getFont(FontFactory.HELVETICA, 12));
            document.add(p3);
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }
        document.close();
        return new ResponseEntity<>("PDF created", HttpStatus.CREATED);
    }
}