package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;


@SpringBootApplication
public class HomebankingApplication {

	LocalDate localDate = LocalDate.now();
	LocalDate localDateOneDay = LocalDate.now().plusDays(1);

	LocalDateTime localDateTime = LocalDateTime.now();

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class);
	}
	@Bean
	public CommandLineRunner initData(ClientRepository repositoryClient, AccountRepository repositoryAccount, TransactionRepository repositoryTransaction) {
		return (args) -> {
			Client melbaMorel = new Client("Melba", "Morel", "Melba@mindhub.com");

			Account firstAccount = new Account("VIN001", this.localDate, 5000);
			Account secondAccount = new Account("VIN002", this.localDateOneDay, 7500);

			Transaction firstTransaction = new Transaction(7000, "Supplies",this.localDateTime, TransactionType.CREDIT);
			Transaction secondTransaction = new Transaction(-8000, "Supplies",this.localDateTime, TransactionType.DEBIT);

			repositoryClient.save(melbaMorel);

			melbaMorel.addAccount(firstAccount);
			melbaMorel.addAccount(secondAccount);

			repositoryAccount.save(firstAccount);
			repositoryAccount.save(secondAccount);

			firstAccount.addTransaction(firstTransaction);
			firstAccount.addTransaction(secondTransaction);

			repositoryTransaction.save(firstTransaction);
			repositoryTransaction.save(secondTransaction);

			Client chloeOBrian = new Client("Chloe", "O'Brian","ChloeOBrian@gmail.com");

			Account thirdAccount = new Account("VIN003", this.localDate, 8000);
			Account fourthAccount = new Account("VIN004", this.localDateOneDay, 10000);

			Transaction thirdTransaction = new Transaction(5000, "Supplies",this.localDateTime, TransactionType.CREDIT);
			Transaction fourthTransaction = new Transaction(-10000, "Supplies",this.localDateTime, TransactionType.DEBIT);

			repositoryClient.save(chloeOBrian);

			chloeOBrian.addAccount(thirdAccount);
			chloeOBrian.addAccount(fourthAccount);

			repositoryAccount.save(thirdAccount);
			repositoryAccount.save(fourthAccount);

			secondAccount.addTransaction(thirdTransaction);
			secondAccount.addTransaction(fourthTransaction);

			repositoryTransaction.save(thirdTransaction);
			repositoryTransaction.save(fourthTransaction);

			repositoryClient.save(new Client("Kim", "Bauer", "KimBeuer@gmail.com"));
			repositoryClient.save(new Client("David", "Palmer", "DavidPalmer@gmail.com"));
			repositoryClient.save(new Client("Michelle", "Dessler","MichelleDessler@gmail.com"));
		};
	}
}
