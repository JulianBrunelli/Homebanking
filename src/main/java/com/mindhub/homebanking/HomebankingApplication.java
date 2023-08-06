package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;


@SpringBootApplication
public class HomebankingApplication {

	LocalDate localDate = LocalDate.now();
	LocalDate localDateTwo = LocalDate.now().plusDays(1);
	LocalDate localDateThree = LocalDate.now();
	LocalDate localDateFour = LocalDate.now().plusDays(1);

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class);
	}
	@Bean
	public CommandLineRunner initData(ClientRepository repositoryClient, AccountRepository repositoryAccount) {
		return (args) -> {
			Account firstAccount = new Account("VIN001", this.localDate, 5000);
			Account secondAccount = new Account("VIN002", this.localDateTwo, 7500);
			Account thirdAccount = new Account("VIN003", this.localDateThree, 8000);
			Account fourthAccount = new Account("VIN004", this.localDateFour, 10000);
			Client melbaMorel = new Client("Melba", "Morel", "Melba@mindhub.com");
			Client chloeOBrian = new Client("Chloe", "O'Brian","ChloeOBrian@gmail.com");
			repositoryClient.save(melbaMorel);
			repositoryClient.save(chloeOBrian);
			melbaMorel.addAccount(firstAccount);
			melbaMorel.addAccount(secondAccount);
			chloeOBrian.addAccount(thirdAccount);
			chloeOBrian.addAccount(fourthAccount);
			repositoryAccount.save(firstAccount);
			repositoryAccount.save(secondAccount);
			repositoryAccount.save(thirdAccount);
			repositoryAccount.save(fourthAccount);
			repositoryClient.save(new Client("Kim", "Bauer", "KimBeuer@gmail.com"));
			repositoryClient.save(new Client("David", "Palmer", "DavidPalmer@gmail.com"));
			repositoryClient.save(new Client("Michelle", "Dessler","MichelleDessler@gmail.com"));
		};
	}
}
