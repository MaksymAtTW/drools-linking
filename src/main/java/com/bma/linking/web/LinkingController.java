package com.bma.linking.web;


import com.bma.linking.service.LinkingService;
import com.bma.linking.domain.LinkingItemSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@RestController
public class LinkingController {

    private static Logger log = LoggerFactory.getLogger(LinkingController.class);

    @Autowired
    LinkingService linkingService;

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    @RequestMapping(value = "/insert/{source}/{ccy}/{amount}/{reference}")
    public String insert(@ModelAttribute("source") String source,
                  @ModelAttribute("ccy") String ccy,
                  @ModelAttribute("amount") BigDecimal amount,
                  @ModelAttribute("reference") String reference) {

        LinkingItemSource sourceValue = LinkingItemSource.valueOf(source);
        String factHandle = linkingService.insert(sourceValue, ccy, amount, reference);

        log.info("Inserted LinkingItem: " + source + "\\" + ccy + "\\" + amount + "\\" + reference + ", FactHandle: " + factHandle);

        return "OK - " + factHandle;
    }

    @RequestMapping(value = "/fireRules")
    public String fireRules() {

        long start = System.currentTimeMillis();

        int result = linkingService.fireRules();

        long time = System.currentTimeMillis() - start;

        log.info("Fired Rules: " + result + ", Time: " + time + " ms");

        return "OK - " + result + ", Time: " + time + " ms";
    }

    @RequestMapping(value = "/facts/all")
    public String getAllFacts() {
        return linkingService.getAllFacts().stream().collect(Collectors.joining("<br/>"));
    }



}
