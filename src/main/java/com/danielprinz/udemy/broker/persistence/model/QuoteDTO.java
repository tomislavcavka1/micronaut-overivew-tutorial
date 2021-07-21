package com.danielprinz.udemy.broker.persistence.model;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class QuoteDTO {

    private Integer    id;
    private BigDecimal volume;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(final BigDecimal volume) {
        this.volume = volume;
    }
}
