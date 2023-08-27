package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class CardControllers {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;


    @RequestMapping("/cards")
    public List<CardDTO> getCards(){
        return cardRepository.findAll().stream().map(card -> new CardDTO(card)).collect(toList());
    }

    @RequestMapping("/clients/cards/{id}")
    public CardDTO getCard(@PathVariable Long id){
        return cardRepository.findById(id).map(card -> new CardDTO(card)).orElse(null);
    }

    @PostMapping( path = "/clients/current/cards")
    public ResponseEntity<Object> newCard(Authentication authentication, @RequestParam CardType type, @RequestParam CardColor color) {

        Client client = clientRepository.findByEmail(authentication.getName());
        String cardHolder = client.getFirstName() + " " + client.getLastName();

        List<Card> cardsType = client.getCards().stream().filter(card -> card.getType() == type).collect(toList());
        List<Card> cardsColor = cardsType.stream().filter(card -> card.getColor() == color).collect(toList());

        if ( type == null || color == null) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (cardsType.size() >=3){
            return new ResponseEntity<>("Failed to create card because the maximum number of cards is 3 for type", HttpStatus.FORBIDDEN);
        }

        if (!cardsColor.isEmpty()){
            return new ResponseEntity<>("Failed to create card because the maximum number of cards is 3 for color", HttpStatus.FORBIDDEN);
        }

        String randomNumberCard;
        do {
            randomNumberCard =  (int) (Math.random()*1000 + 9999)
                        + "-" + (int) (Math.random()*1000 + 9999)
                        + "-" + (int) (Math.random()*1000 + 9999)
                        + "-" + (int) (Math.random()*1000 + 9999);
        }while (cardRepository.findByNumber(randomNumberCard)!=null);

        int cvv;
        do {
            cvv = (int) (Math.random()*100 + 999);
        }while (cardRepository.findByCvv(cvv)!=null);

        Card card = new Card( cardHolder,type,color,randomNumberCard,cvv,LocalDate.now(),LocalDate.now().plusYears(5));
        client.addCard(card);
        cardRepository.save(card);

        return new ResponseEntity<>("Your card was successfully created", HttpStatus.CREATED);
    }
}
