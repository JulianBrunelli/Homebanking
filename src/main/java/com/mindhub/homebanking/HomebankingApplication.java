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
	private PasswordEncoder passwordEncoder;
	private final LocalDate localDate = LocalDate.now();
	private final LocalDate localDateExpires = LocalDate.now().plusYears(5);
	private final LocalDate localDateOneDay = LocalDate.now().plusDays(1);
	private final LocalDateTime localDateTime = LocalDateTime.now();
	private final List<Integer> mortgageLoan = List.of(12,24,36,48,60);
	private final List<Integer> carLoan = List.of(6,12,24,36);
	private final List<Integer> personalLoan = List.of(6,12,24);

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository repositoryClient, AccountRepository repositoryAccount, TransactionRepository repositoryTransaction,
									  LoanRepository repositoryLoan, ClientLoanRepository repositoryClientLoan, CardRepository repositoryCard) {
		return (args) -> {

			Loan mortgageLoan = new Loan("Mortgage", 500000, this.mortgageLoan, 10);
			Loan carLoan = new Loan("Car", 300000, this.carLoan,5);
			Loan personalLoan = new Loan("Personal", 100000, this.personalLoan,5);

			repositoryLoan.save(mortgageLoan);
			repositoryLoan.save(carLoan);
			repositoryLoan.save(personalLoan);

			Client admin = new Client("Julian","Brunelli","julianbrunelli@outlook.com", passwordEncoder.encode("123456"));
			repositoryClient.save(admin);

			Client melbaMorel = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("1234"));

			Account firstAccount = new Account("VIN-001", this.localDate, 5000, true, AccountType.CURRENT);
			Account secondAccount = new Account("VIN-002", this.localDateOneDay, 7500, true, AccountType.SAVINGS);

			Transaction firstTransaction = new Transaction(7000, "Supplies",this.localDateTime, TransactionType.CREDIT, 0);
			Transaction secondTransaction = new Transaction(-8000, "Supplies",this.localDateTime, TransactionType.DEBIT, 0);

			ClientLoan melbaMortageLoan = new ClientLoan(400000, 60,true);
			ClientLoan melbaPersonalLoan = new ClientLoan(50000, 12,true);

			Card melbaMorelFirstCard = new Card(melbaMorel.getFirstName()+" "+melbaMorel.getLastName(), CardType.DEBIT, CardColor.GOLD,
					"4325-5667-4253-9896", 444, this.localDate, this.localDateExpires, true);
			Card melbaMorelSecondCard = new Card(melbaMorel.getFirstName()+" "+melbaMorel.getLastName(), CardType.CREDIT, CardColor.TITANIUM,
					"4000-4546-5734-2351", 324, this.localDate, this.localDateExpires, true);

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

			Account thirdAccount = new Account("VIN-003", this.localDate, 8000, true, AccountType.CURRENT);
			Account fourthAccount = new Account("VIN-004", this.localDateOneDay, 10000, true, AccountType.SAVINGS);

			Transaction thirdTransaction = new Transaction(5000, "Supplies",this.localDateTime, TransactionType.CREDIT, 0);
			Transaction fourthTransaction = new Transaction(-10000, "Supplies",this.localDateTime, TransactionType.DEBIT, 0);

			ClientLoan chloePersonalLoan = new ClientLoan(100000, 24,true);
			ClientLoan chloeCarlLoan = new ClientLoan(200000, 36,true);

			Card chloeOBrianFirstCard = new Card(chloeOBrian.getFirstName()+" "+chloeOBrian.getLastName(), CardType.DEBIT, CardColor.GOLD,
					"4004-8646-4663-2424", 123, this.localDate, this.localDateExpires, true);
			Card chloeOBrianSecondCard = new Card(chloeOBrian.getFirstName()+" "+chloeOBrian.getLastName(), CardType.CREDIT, CardColor.SILVER,
					"4006-4244-5179-2266", 424, this.localDate, this.localDateExpires, true);

			repositoryClient.save(chloeOBrian);

			chloeOBrian.addAccount(thirdAccount);
			chloeOBrian.addAccount(fourthAccount);

			repositoryAccount.save(thirdAccount);
			repositoryAccount.save(fourthAccount);

			thirdAccount.addTransaction(thirdTransaction);
			fourthAccount.addTransaction(fourthTransaction);

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
