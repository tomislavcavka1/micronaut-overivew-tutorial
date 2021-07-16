package com.danielprinz.udemy.broker.account;

import com.danielprinz.udemy.broker.model.Symbol;
import com.danielprinz.udemy.broker.model.WatchList;
import com.danielprinz.udemy.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.Single;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.DELETE;
import static io.micronaut.http.HttpRequest.GET;
import static io.micronaut.http.HttpRequest.PUT;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class WatchListControllerReactiveTest {

    private static final Logger LOG             = LoggerFactory.getLogger(WatchListControllerReactiveTest.class);
    private static final UUID   TEST_ACCOUNT_ID = WatchListControllerReactive.ACCOUNT_ID;

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/account/watchlist-reactive")
    RxHttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void returnsEmptyWatchListForAccount() {
        final Single<WatchList> result = client.retrieve(GET("/"), WatchList.class).singleOrError();
        assertTrue(result.blockingGet().getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnsWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                                           .map(Symbol::new)
                                           .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        final WatchList result = client.toBlocking().retrieve("/", WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void returnsWatchListForAccountAsSingle() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                                           .map(Symbol::new)
                                           .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        final WatchList result = client.toBlocking().retrieve("/single", WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                                           .map(Symbol::new)
                                           .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        final HttpResponse<Object> added = client.toBlocking().exchange(PUT("/", watchList));
        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
    }

    @Test
    void canDeleteWatchListForAccount() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                                           .map(Symbol::new)
                                           .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

        final HttpResponse<Object> deleted = client.toBlocking().exchange(DELETE("/" + TEST_ACCOUNT_ID));
        assertEquals(HttpStatus.OK, deleted.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }
}
