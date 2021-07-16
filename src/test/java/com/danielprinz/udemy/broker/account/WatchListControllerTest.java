package com.danielprinz.udemy.broker.account;

import com.danielprinz.udemy.broker.model.Symbol;
import com.danielprinz.udemy.broker.model.WatchList;
import com.danielprinz.udemy.broker.store.InMemoryAccountStore;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@MicronautTest
public class WatchListControllerTest {

    private static final Logger LOG               = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID   TEST_ACCOUNT_ID   = WatchListController.ACCOUNT_ID;
    private static final String ACCOUNT_WATCHLIST = "/account/watchlist/";

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void unauthorizedAccessIsForbidden() {
        try {
            client.toBlocking().retrieve(ACCOUNT_WATCHLIST);
            fail("Should fail if no exception is thrown");
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
        }
    }

    @Test
    void returnsEmptyWatchListForAccount() {
        final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        var request = GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnsWatchListForAccount() {
        final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                                           .map(Symbol::new)
                                           .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        var request = GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount() {
        final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                                           .map(Symbol::new)
                                           .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        var request = PUT(ACCOUNT_WATCHLIST, watchList)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final HttpResponse<Object> added = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, added.getStatus());
        final WatchList watchList1 = store.getWatchList(TEST_ACCOUNT_ID);
        assertEquals(watchList, watchList1);
    }

    @Test
    void canDeleteWatchListForAccount() {
        final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                                           .map(Symbol::new)
                                           .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

        var request = DELETE(ACCOUNT_WATCHLIST + TEST_ACCOUNT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final HttpResponse<Object> deleted = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, deleted.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    private BearerAccessRefreshToken givenMyUserIsLoggedIn() {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("my-user", "secret");
        var login = HttpRequest.POST("/login", credentials);
        var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        final BearerAccessRefreshToken token = response.body();
        assertNotNull(token);
        assertEquals("my-user", token.getUsername());
        LOG.debug("Login Bearer Token: {} expires in {}", token.getAccessToken(), token.getExpiresIn());
        return token;
    }
}
