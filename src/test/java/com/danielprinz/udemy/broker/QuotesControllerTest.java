package com.danielprinz.udemy.broker;

import com.danielprinz.udemy.broker.error.CustomError;
import com.danielprinz.udemy.broker.model.Quote;
import com.danielprinz.udemy.broker.model.Symbol;
import com.danielprinz.udemy.broker.store.InMemoryStore;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static io.micronaut.http.HttpRequest.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class QuotesControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(QuotesControllerTest.class);

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Inject
    InMemoryStore store;

    @Test
    void returnsQuotePerSymbol() {
        final Quote apple = initRandomQuote("APPL");
        store.update(apple);

        final Quote amazon = initRandomQuote("AMZN");
        store.update(amazon);

        final Quote appleResult = client.toBlocking().retrieve(GET("/quotes/APPL"), Quote.class);
        LOG.debug("Result: {}", appleResult);
        assertThat(apple).usingRecursiveComparison().isEqualTo(appleResult);

        final Quote amazonResult = client.toBlocking().retrieve(GET("/quotes/AMZN"), Quote.class);
        LOG.debug("Result: {}", amazonResult);
        assertThat(amazon).usingRecursiveComparison().isEqualTo(amazonResult);
    }

    @Test
    void returnsNotFoundOnUnsupportedSymbol() {
        try {
            client.toBlocking().retrieve(GET("/quotes/UNSUPPORTED"),
                                         Argument.of(Quote.class),
                                         Argument.of(CustomError.class));
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getResponse().getStatus());
            LOG.debug("Body: {}", e.getResponse().getBody(CustomError.class));
            final Optional<CustomError> customError = e.getResponse().getBody(CustomError.class);
            assertTrue(customError.isPresent());
            assertEquals(404, customError.get().getStatus());
            assertEquals("NOT_FOUND", customError.get().getError());
            assertEquals("quote for symbol not available", customError.get().getMessage());
            assertEquals("/quotes/UNSUPPORTED", customError.get().getPath());
        }
    }

    private Quote initRandomQuote(String symbol) {
        return Quote.builder()
                    .symbol(new Symbol(symbol))
                    .bid(randomValue())
                    .ask(randomValue())
                    .lastPrice(randomValue())
                    .volume(randomValue())
                    .build();
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
    }

}
