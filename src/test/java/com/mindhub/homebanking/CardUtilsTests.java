package com.mindhub.homebanking;

import com.mindhub.homebanking.utils.CardUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

    @SpringBootTest
    public class CardUtilsTests {
        @Test
        public void cardNumberIsCreated(){
            String cardNumber = CardUtils.getCardNumber();
            assertThat(cardNumber,is(not(emptyOrNullString())));
        }
        @Test
        public void cardNumberIsString(){
            String cardNumber = CardUtils.getCardNumber();
            assertThat(cardNumber, isA(String.class));
        }
        @Test
        public void cvvIsInteger(){
            int cvv = CardUtils.getCvv();
            assertThat(cvv,isA(int.class));
        }
        @Test
        public void existsCvv(){
            int cvv = CardUtils.getCvv();
            assertThat(cvv, is(notNullValue()));
        }
    }
