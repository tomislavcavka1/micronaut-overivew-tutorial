package com.danielprinz.udemy.broker.account;

import com.danielprinz.udemy.broker.model.WatchList;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.util.UUID;

@Client("/")
public interface JWTWatchListClient {

    public static final String ACCOUNT_WATCHLIST_REACTIVE_URL = "/account/watchlist-reactive";

    @Post("/login")
    BearerAccessRefreshToken login(@Body UsernamePasswordCredentials credentials);

    @Get(ACCOUNT_WATCHLIST_REACTIVE_URL)
    Flowable<WatchList> retrieveWatchList(@Header String authorization);

    @Get(ACCOUNT_WATCHLIST_REACTIVE_URL + "/single")
    Single<WatchList> retrieveWatchListAsSingle(@Header String authorization);

    @Put(ACCOUNT_WATCHLIST_REACTIVE_URL)
    HttpResponse<WatchList> updateWatchList(@Header String authorization, @Body WatchList watchList);

    @Delete(ACCOUNT_WATCHLIST_REACTIVE_URL + "/{accountId}")
    HttpResponse<WatchList> deleteWatchList(@Header String authorization, @PathVariable UUID accountId);
}
