package com.bma.linking.facts;

import com.bma.linking.domain.LinkingItemSource;
import com.bma.linking.domain.LinkingItemState;

import java.math.BigDecimal;

public class LinkingItemFact {

    LinkingItemSource source;
    BigDecimal amount;
    String currency;
    String reference;

    LinkingItemState state = LinkingItemState.NEW;

    public static LinkingItemFact valueOf(LinkingItemSource source, String ccy, BigDecimal amount, String reference) {
        LinkingItemFact fact = new LinkingItemFact();
        fact.source = source;
        fact.amount = amount;
        fact.currency = ccy;
        fact.reference = reference;
        return fact;
    }

    public LinkingItemSource getSource() {
        return source;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getReference() {
        return reference;
    }

    public LinkingItemState getState() {
        return state;
    }


    public void markAsLinked() {
        state = LinkingItemState.LINKED;
    }

    public boolean isNew() {
        return state == LinkingItemState.NEW;
    }

    public boolean isLinked() {
        return state == LinkingItemState.LINKED;
    }

    @Override
    public String toString() {
        return "LinkingItemFact{" +
                "source=" + source +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", reference='" + reference + '\'' +
                '}';
    }
}
