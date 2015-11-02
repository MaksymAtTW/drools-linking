package com.bma.linking.service;

import com.bma.linking.domain.LinkingItemSource;
import com.bma.linking.facts.BankTransactionLinkFact;
import com.bma.linking.facts.LinkingItemFact;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class LinkingService {

    private static Logger log = LoggerFactory.getLogger(LinkingService.class);


    @Autowired
    KieContainer kieContainer;

    KieSession session;


    @PostConstruct
    public void init() {
        session = kieContainer.newKieSession("LinkingSession");
    }


    public String insert(LinkingItemSource source, String ccy, BigDecimal amount, String reference) {

        LinkingItemFact fact = LinkingItemFact.valueOf(source, ccy, amount, reference);

        return session.insert(fact).toExternalForm();
    }

    public int fireRules() {

        int firedRulesCount = session.fireAllRules();

        log.debug("Fired Rules Count: " + firedRulesCount);

        if (firedRulesCount > 0) {
            Collection<FactHandle> linkedItems = session.getFactHandles((Object obj) -> {
                return (obj instanceof LinkingItemFact && ((LinkingItemFact) obj).isLinked()) || (obj instanceof BankTransactionLinkFact);
            });

            log.debug("Found facts to delete: " + linkedItems.size());

            linkedItems.stream().forEach(session::delete);
        }

        return firedRulesCount;

    }

    public Collection<String> getAllFacts() {
        return session.getFactHandles().stream().
                map(fact -> session.getObject(fact).toString() + " - " + fact.toExternalForm()).
                collect(Collectors.toList());
    }
}
