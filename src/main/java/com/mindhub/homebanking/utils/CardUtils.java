package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.service.CardService;

public final class CardUtils {
    public static String getCardNumber() {
        return (int) (Math.random() * (9000 - 4000) + 4000)
                + "-" + (int) (Math.random() * (9000 - 4000) + 4000)
                + "-" + (int) (Math.random() * (9000 - 4000) + 4000)
                + "-" + (int) (Math.random() * (9000 - 4000) + 4000);
    }

    public static int getCvv() {
        return (int) (Math.random()*(999 - 100) + 100);
    }
}
