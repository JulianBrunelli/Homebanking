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

import javax.swing.text.Document;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
            @RequestParam String originAccountNumber, @RequestParam String destinationAccountNumber){

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

//    @GetMapping("/transactions/pdf")
//    public ResponseEntity<Object> createPdf(@RequestParam String startDate, @RequestParam String endDate, @RequestParam String accountNumber,
//                                             Authentication authentication) throws DocumentException, IOException {
//        Client current = clientService.findByEmail(authentication.getName());
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//
//        if (current == null) {
//            return new ResponseEntity<>("you are not allowed to see this", HttpStatus.FORBIDDEN);
//        }
//        if (accountService.findByNumber(accountNumber) == null) {
//            return new ResponseEntity<>("this account don't exist", HttpStatus.BAD_REQUEST);
//        }
//        if (startDate.isBlank()) {
//            return new ResponseEntity<>("Please, fill the date requeriment", HttpStatus.BAD_REQUEST);
//        }
//        if (endDate.isBlank()) {
//            return new ResponseEntity<>("Please, fill the date end requeriment",HttpStatus.BAD_REQUEST);
//        }
//        if (startDate.equals(endDate)) {
//            return new ResponseEntity<>("You cant use the same date", HttpStatus.BAD_REQUEST);
//        }
//        LocalDate localDateStart = LocalDate.parse(startDate, formatter);
//        LocalDate localDateEnd = LocalDate.parse(endDate, formatter);
//        List<Transaction> transfer = transactionService.findByDateBetweenAndAccountNumber(localDateStart, localDateEnd, accountNumber);
//        if (transfer.size() <= 0){
//            return new ResponseEntity<>("No transactions finded.",HttpStatus.NOT_FOUND);
//        }
//
//        com.lowagie.text.Document document = new com.lowagie.text.Document();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        PdfWriter.getInstance(document, out);
//        document.open();
//        PdfPTable tableTitle = new PdfPTable(1);
//        PdfPCell cell = new PdfPCell();
//        cell.setBorder(PdfPCell.NO_BORDER);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setPadding(10);
//        cell.addElement(new Paragraph("Your transactions", new Font(Font.HELVETICA, 24)));
//        tableTitle.addCell(cell);
//        document.add(tableTitle);
//
//        PdfPTable table = new PdfPTable(4);
//        table.addCell("Type");
//        table.addCell("Description");
//        table.addCell("Amount");
//        table.addCell("Date");
//
//        for (Transaction transaction : transfer) {
//            table.addCell(transaction.getTransactionType().toString());
//            table.addCell(transaction.getDescription());
//            table.addCell(String.valueOf(transaction.getAmount()));
//            table.addCell(transaction.getDate().format(formatter));
//        }
//        document.add(table);
//        PdfPCell spacerCell = new PdfPCell();
//        spacerCell.setFixedHeight(50);
//        spacerCell.setBorder(PdfPCell.NO_BORDER);
//        spacerCell.setColspan(4);
//        document.add(spacerCell);
//        PdfPTable logo = new PdfPTable(2);
//        logo.setWidthPercentage(100);
//        Image img = Image.getInstance("C:\\Users\\User\\Desktop\\home banking\\src\\main \\resources\\static\\web\\images\\bank.icon.png");
//        img.scaleToFit(50, 50);
//        img.setAbsolutePosition(50, 50);
//        img.setAlignment(Image.ALIGN_BASELINE);
//        PdfPCell imageCell = new PdfPCell(img);
//        imageCell.setBorder(PdfPCell.NO_BORDER);
//        logo.addCell(imageCell);
//        PdfPCell textCell = new PdfPCell();
//        textCell.setBorder(PdfPCell.NO_BORDER);
//        textCell.addElement(new Phrase("MindHub Brothers, pa"));
//        logo.addCell(textCell);
//
//        document.add(logo);
//        document.close();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=transactions-Table.pdf");
//        byte[] pdf = out.toByteArray();
//        return new ResponseEntity<>(pdf,headers, HttpStatus.CREATED);
//    }
}