package com.bma.linking.facts;

public class BankTransactionLinkFact {

    LinkingItemFact bankTransaction;
    LinkingItemFact targetObject;

    public BankTransactionLinkFact(LinkingItemFact bankTransaction, LinkingItemFact targetObject) {
        this.bankTransaction = bankTransaction;
        this.targetObject = targetObject;
    }
}
