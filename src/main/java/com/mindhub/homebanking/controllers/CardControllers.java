package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.service.CardService;
import com.mindhub.homebanking.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.mindhub.homebanking.utils.CardUtils.getCardNumber;
import static com.mindhub.homebanking.utils.CardUtils.getCvv;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping( path = "/api")
public class CardControllers {
    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;
    private final int cvv = getCvv();

    @GetMapping("/cards")
    public List<CardDTO> getCards(){
        return cardService.getCardsDTO();
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> newCard(Authentication authentication, @RequestParam String type, @RequestParam String color) {

        Client client = clientService.findByEmail(authentication.getName());
        String cardHolder = client.getFirstName() + " " + client.getLastName();

        if (type.isBlank()) {
            return new ResponseEntity<>("Please select a type", HttpStatus.FORBIDDEN);
        }
        if(color.isBlank()){
            return new ResponseEntity<>("Please select a color", HttpStatus.FORBIDDEN);
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
            return new ResponseEntity<>("Failed to create card because you can only have 3 cards per type", HttpStatus.FORBIDDEN);
        }

        if (!cardsColor.isEmpty()){
            return new ResponseEntity<>("Could not create card because you can only have 1 color per type", HttpStatus.FORBIDDEN);
        }

        String randomNumberCard = "";
        do {
            randomNumberCard = getCardNumber();
        }while (cardService.findByNumber(randomNumberCard)!=null);

        int cvv = getCvv();

        Card card = new Card( cardHolder,CardType.valueOf(type),CardColor.valueOf(color),randomNumberCard,cvv,LocalDate.now(),LocalDate.now().plusYears(5),true);
        client.addCard(card);
        cardService.saveCard(card);

        return new ResponseEntity<>("Your card was successfully created", HttpStatus.CREATED);
    }

    @PatchMapping("/clients/current/cards/deactivate")
    public ResponseEntity<Object> disableCard(@RequestParam long id,Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        Card card = cardService.findById(id);
        boolean cardClientAuth = client.getCards().contains(card);
        if(card == null){
            return new ResponseEntity<>("Card not found", HttpStatus.FORBIDDEN);
        }
        if(!cardClientAuth){
            return new ResponseEntity<>("The letter does not belong to the authenticated client", HttpStatus.FORBIDDEN);
        }
        if(!card.isActive()){
            return new ResponseEntity<>("Card already disabled", HttpStatus.FORBIDDEN);
        }
        card.setActive(false);
        cardService.saveCard(card);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
