package com.danielprinz.udemy.broker.model;

import java.util.ArrayList;
import java.util.List;

public class WatchList {

    private List<Symbol> symbols = new ArrayList<>();

    public WatchList() {
    }

    public WatchList(final List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(final List<Symbol> symbols) {
        this.symbols = symbols;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof WatchList))
            return false;

        final WatchList watchList = (WatchList) o;

        return getSymbols().equals(watchList.getSymbols());
    }

    @Override
    public int hashCode() {
        return getSymbols().hashCode();
    }
}
