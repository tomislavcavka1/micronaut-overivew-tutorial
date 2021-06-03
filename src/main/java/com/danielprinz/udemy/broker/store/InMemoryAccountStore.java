package com.danielprinz.udemy.broker.store;

import com.danielprinz.udemy.broker.model.WatchList;

import java.util.UUID;

public class InMemoryAccountStore {

    public WatchList getWatchList(final UUID accountId) {
        throw new UnsupportedOperationException(InMemoryAccountStore.class.getName() + "::getWatchList");
    }

    public WatchList updateWatchList(final UUID accountId, final WatchList watchList) {
        throw new UnsupportedOperationException(InMemoryAccountStore.class.getName() + "::updateWatchList");
    }

}
