package com.danielprinz.udemy.broker.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Symbol", description = "Abbreviation to uniquely identify public trades shares of a stock.")
public class Symbol {

    private String value;

    public Symbol() {
    }

    public Symbol(final String value) {
        this.value = value;
    }

    @Schema(description = "symbol value", minLength = 1, maxLength = 5)
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Symbol))
            return false;

        final Symbol symbol = (Symbol) o;

        return getValue().equals(symbol.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}
