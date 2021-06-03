package com.danielprinz.udemy.broker.account;

import com.danielprinz.udemy.broker.model.WatchList;
import com.danielprinz.udemy.broker.store.InMemoryAccountStore;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;

import java.util.UUID;

@Controller("/account/watchlist")
public class WatchListController {

    private static final UUID                 ACCOUNT_ID = UUID.randomUUID();
    private final        InMemoryAccountStore store;

    public WatchListController(final InMemoryAccountStore store) {
        this.store = store;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get() {
        return store.getWatchList(ACCOUNT_ID);
    }

    @Put(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);
    }

}
