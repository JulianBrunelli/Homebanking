package com.mindhub.homebanking.service;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;

import java.util.List;

public interface CardService {
    List<CardDTO> getCardsDTO();
    Card findByNumber(String number);

    void saveCard(Card card);

    Card findById(long id);
}
