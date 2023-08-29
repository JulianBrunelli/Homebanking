package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {
	@Autowired
	PasswordEncoder passwordEncoder;
	LocalDate localDate = LocalDate.now();
	LocalDate localDateExpires = LocalDate.now().plusYears(5);
	LocalDate localDateOneDay = LocalDate.now().plusDays(1);
	LocalDateTime localDateTime = LocalDateTime.now();
	List<Integer> mortgageLoan = List.of(12,24,36,48,60);
	List<Integer> carLoan = List.of(6,12,24,36);
	List<Integer> personalLoan = List.of(6,12,24);

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class);
	}
	@Bean
	public CommandLineRunner initData(ClientRepository repositoryClient, AccountRepository repositoryAccount, TransactionRepository repositoryTransaction,
									  LoanRepository repositoryLoan, ClientLoanRepository repositoryClientLoan, CardRepository repositoryCard) {
		return (args) -> {

			Loan mortgageLoan = new Loan("Mortgage", 500000, this.mortgageLoan);
			Loan carLoan = new Loan("Car", 300000, this.carLoan);
			Loan personalLoan = new Loan("Personal", 100000, this.personalLoan);

			repositoryLoan.save(mortgageLoan);
			repositoryLoan.save(carLoan);
			repositoryLoan.save(personalLoan);

			Client admin = new Client("Julian","Brunelli","julianbrunelli@outlook.com", passwordEncoder.encode("123456"));
			repositoryClient.save(admin);

			Client melbaMorel = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("1234"));

			Account firstAccount = new Account("VIN-001", this.localDate, 5000);
			Account secondAccount = new Account("VIN-002", this.localDateOneDay, 7500);

			Transaction firstTransaction = new Transaction(7000, "Supplies",this.localDateTime, TransactionType.CREDIT);
			Transaction secondTransaction = new Transaction(-8000, "Supplies",this.localDateTime, TransactionType.DEBIT);

			ClientLoan melbaMortageLoan = new ClientLoan(400000, 60);
			ClientLoan melbaPersonalLoan = new ClientLoan(50000, 12);

			Card melbaMorelFirstCard = new Card(melbaMorel.getFirstName()+" "+melbaMorel.getLastName(), CardType.DEBIT, CardColor.GOLD,
					"4325-5667-4253-9896", 444, this.localDate, this.localDateExpires);
			Card melbaMorelSecondCard = new Card(melbaMorel.getFirstName()+" "+melbaMorel.getLastName(), CardType.CREDIT, CardColor.TITANIUM,
					"4000-4546-5734-2351", 324, this.localDate, this.localDateExpires);

			repositoryClient.save(melbaMorel);

			melbaMorel.addAccount(firstAccount);
			melbaMorel.addAccount(secondAccount);

			repositoryAccount.save(firstAccount);
			repositoryAccount.save(secondAccount);

			firstAccount.addTransaction(firstTransaction);
			firstAccount.addTransaction(secondTransaction);

			repositoryTransaction.save(firstTransaction);
			repositoryTransaction.save(secondTransaction);

			melbaMorel.addClientLoan(melbaMortageLoan);
			melbaMorel.addClientLoan(melbaPersonalLoan);

			repositoryClientLoan.save(melbaMortageLoan);
			repositoryClientLoan.save(melbaPersonalLoan);

			mortgageLoan.addClientLoan(melbaMortageLoan);
			personalLoan.addClientLoan(melbaPersonalLoan);

			repositoryClientLoan.save(melbaMortageLoan);
			repositoryClientLoan.save(melbaPersonalLoan);

			melbaMorel.addCard(melbaMorelFirstCard);
			melbaMorel.addCard(melbaMorelSecondCard);

			repositoryCard.save(melbaMorelFirstCard);
			repositoryCard.save(melbaMorelSecondCard);

			Client chloeOBrian = new Client("Chloe", "O'Brian","chloeobrian@gmail.com",passwordEncoder.encode("Hola2341"));

			Account thirdAccount = new Account("VIN003", this.localDate, 8000);
			Account fourthAccount = new Account("VIN004", this.localDateOneDay, 10000);

			Transaction thirdTransaction = new Transaction(5000, "Supplies",this.localDateTime, TransactionType.CREDIT);
			Transaction fourthTransaction = new Transaction(-10000, "Supplies",this.localDateTime, TransactionType.DEBIT);

			ClientLoan chloePersonalLoan = new ClientLoan(100000, 24);
			ClientLoan chloeCarlLoan = new ClientLoan(200000, 36);

			Card chloeOBrianFirstCard = new Card(chloeOBrian.getFirstName()+" "+chloeOBrian.getLastName(), CardType.DEBIT, CardColor.GOLD,
					"4004-8646-4663-2424", 123, this.localDate, this.localDateExpires);
			Card chloeOBrianSecondCard = new Card(chloeOBrian.getFirstName()+" "+chloeOBrian.getLastName(), CardType.CREDIT, CardColor.SILVER,
					"4006-4244-5179-2266", 424, this.localDate, this.localDateExpires);

			repositoryClient.save(chloeOBrian);

			chloeOBrian.addAccount(thirdAccount);
			chloeOBrian.addAccount(fourthAccount);

			repositoryAccount.save(thirdAccount);
			repositoryAccount.save(fourthAccount);

			secondAccount.addTransaction(thirdTransaction);
			secondAccount.addTransaction(fourthTransaction);

			repositoryTransaction.save(thirdTransaction);
			repositoryTransaction.save(fourthTransaction);

			chloeOBrian.addClientLoan(chloePersonalLoan);
			chloeOBrian.addClientLoan(chloeCarlLoan);

			repositoryClientLoan.save(chloePersonalLoan);
			repositoryClientLoan.save(chloeCarlLoan);

			personalLoan.addClientLoan(chloePersonalLoan);
			carLoan.addClientLoan(chloeCarlLoan);

			repositoryClientLoan.save(chloePersonalLoan);
			repositoryClientLoan.save(chloeCarlLoan);

			chloeOBrian.addCard(chloeOBrianFirstCard);
			chloeOBrian.addCard(chloeOBrianSecondCard);

			repositoryCard.save(chloeOBrianFirstCard);
			repositoryCard.save(chloeOBrianSecondCard);
		};
	}
}
