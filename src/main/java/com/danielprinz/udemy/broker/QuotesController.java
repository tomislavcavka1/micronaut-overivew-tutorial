package com.danielprinz.udemy.broker;

import com.danielprinz.udemy.broker.error.CustomError;
import com.danielprinz.udemy.broker.model.Quote;
import com.danielprinz.udemy.broker.store.InMemoryStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

import java.util.Optional;

@Controller("/quotes")
public class QuotesController {

    private InMemoryStore store;

    public QuotesController(final InMemoryStore store) {
        this.store = store;
    }

    @Get("/{symbol}")
    public HttpResponse getQuote(@PathVariable String symbol) {
        final Optional<Quote> maybeQuote = store.fetchQuote(symbol);
        if (maybeQuote.isEmpty()) {
            final CustomError notFound = CustomError.builder()
                                                    .status(HttpStatus.NOT_FOUND.getCode())
                                                    .error(HttpStatus.NOT_FOUND.name())
                                                    .message("quote for symbol not available")
                                                    .path("/quotes/" + symbol)
                                                    .build();
            return HttpResponse.notFound(notFound);
        }
        return HttpResponse.ok(maybeQuote.get());
    }
}
