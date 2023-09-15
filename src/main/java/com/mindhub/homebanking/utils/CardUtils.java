package com.mindhub.homebanking.utils;

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

    public static String getAccountNumber() {
        String random;
        int number = (int) (Math.random()*(10000000 + 99999999) + 10000000);
        random = "VIN-" + number;
        return random;
    }

}
