package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.service.CardService;

public final class CardUtils {
    public static String getCardNumber() {
        String randomNumberCard = (int) (Math.random() * (9000 - 4000) + 4000)
                + "-" + (int) (Math.random() * (9000 - 4000) + 4000)
                + "-" + (int) (Math.random() * (9000 - 4000) + 4000)
                + "-" + (int) (Math.random() * (9000 - 4000) + 4000);
        return randomNumberCard;
    }

    public static int getCvv() {
        int cvv = (int) (Math.random()*(999 - 100) + 100);
        return cvv;
    }
}
