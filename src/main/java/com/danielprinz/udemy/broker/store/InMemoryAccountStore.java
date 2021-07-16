package com.danielprinz.udemy.broker.store;

import com.danielprinz.udemy.broker.model.WatchList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class InMemoryAccountStore {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryAccountStore.class);

    private final HashMap<UUID, WatchList> watchListsPerAccount = new HashMap<>();

    public WatchList getWatchList(final UUID accountId) {
        final WatchList orDefault = watchListsPerAccount.getOrDefault(accountId, new WatchList());
        return orDefault;
    }

    public WatchList updateWatchList(final UUID accountId, final WatchList watchList) {
        watchListsPerAccount.put(accountId, watchList);
        return getWatchList(accountId);
    }

    public void deleteWatchList(final UUID accountId) {
        watchListsPerAccount.remove(accountId);
    }
}
