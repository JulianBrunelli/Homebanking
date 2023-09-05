package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.service.CardService;
import com.mindhub.homebanking.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping( path = "/api")
public class CardControllers {
    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;


    @GetMapping("/cards")
    public List<CardDTO> getCards(){
        return cardService.getCardsDTO();
    }


    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> newCard(Authentication authentication, @RequestParam String type, @RequestParam String color) {

        Client client = clientService.findByEmail(authentication.getName());
        String cardHolder = client.getFirstName() + " " + client.getLastName();

        if (type.isBlank() || color.isBlank()) {
            return new ResponseEntity<>("Please select values", HttpStatus.FORBIDDEN);
        }
        if (!type.equals("DEBIT") && !type.equals("CREDIT")) {
            return new ResponseEntity<>("Please select an valid type", HttpStatus.FORBIDDEN);
        }
        if (!color.equals("GOLD") && !color.equals("SILVER") && !color.equals("TITANIUM")) {
            return new ResponseEntity<>("Please select an valid color", HttpStatus.FORBIDDEN);
        }

        List<Card> cardsType = client.getCards().stream().filter(card -> card.getType().equals(CardType.valueOf(type))).collect(toList());
        List<Card> cardsColor = cardsType.stream().filter(card -> card.getColor().equals(CardColor.valueOf(color))).collect(toList());


        if (cardsType.size() >= 3){
            return new ResponseEntity<>("Failed to create card because the maximum number of cards is 3 for type", HttpStatus.FORBIDDEN);
        }

        if (!cardsColor.isEmpty()){
            return new ResponseEntity<>("Failed to create card because the maximum number is 1 color for type", HttpStatus.FORBIDDEN);
        }

        String randomNumberCard;
        do {
            randomNumberCard =  (int) (Math.random()*(9000 - 4000) + 4000)
                        + "-" + (int) (Math.random()*(9000 - 4000) + 4000)
                        + "-" + (int) (Math.random()*(9000 - 4000) + 4000)
                        + "-" + (int) (Math.random()*(9000 - 4000) + 4000);
        }while (cardService.findByNumber(randomNumberCard)!=null);

        int cvv = (int) (Math.random()*(999 - 100) + 100);

        Card card = new Card( cardHolder,CardType.valueOf(type),CardColor.valueOf(color),randomNumberCard,cvv,LocalDate.now(),LocalDate.now().plusYears(5));
        client.addCard(card);
        cardService.saveCard(card);

        return new ResponseEntity<>("Your card was successfully created", HttpStatus.CREATED);
    }
}
