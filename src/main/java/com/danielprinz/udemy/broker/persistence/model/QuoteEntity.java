package com.danielprinz.udemy.broker.persistence.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity(name = "quote")
@Table(name = "quotes", schema = "mn")
@Data
public class QuoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer      id;
    @ManyToOne(targetEntity = SymbolEntity.class)
    @JoinColumn(name = "symbol", referencedColumnName = "value")
    private SymbolEntity symbol;
    private BigDecimal   bid;
    private BigDecimal   ask;
    @Column(name = "last_price")
    private BigDecimal   lastPrice;
    private BigDecimal   volume;
}
