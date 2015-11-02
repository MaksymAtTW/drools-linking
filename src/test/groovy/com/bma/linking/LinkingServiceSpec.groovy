package com.bma.linking

import com.bma.linking.facts.BankTransactionLinkFact
import com.bma.linking.facts.LinkingItemFact
import com.bma.linking.domain.LinkingItemSource
import groovyx.net.http.RESTClient
import org.drools.core.event.DebugRuleRuntimeEventListener
import org.kie.api.KieServices
import org.kie.api.runtime.ClassObjectFilter
import org.kie.api.runtime.KieContainer
import org.kie.api.runtime.ObjectFilter
import org.kie.api.runtime.rule.FactHandle
import spock.lang.Specification

class LinkingServiceSpec extends Specification {

	KieContainer container

	def setup() {
		container = KieServices.Factory.get().getKieClasspathContainer()
	}

	def "new session can be created"() {
		when:

			def session = container.newKieSession()

		then:
			session
	}

	def "bank transaction and exchange instruction can be linked"() {
		given:
			def bt = new LinkingItemFact(source: LinkingItemSource.BANK_TRANSACTION, amount: -100, currency: "GBP")
			def ei = new LinkingItemFact(source: LinkingItemSource.EXCHANGE_INSTRUCTION, amount: 100, currency: "GBP")


		and:
			def session = container.newKieSession()
			session.addEventListener(new DebugRuleRuntimeEventListener())

		when:


			session.insert(bt)
			session.insert(ei)
			session.fireAllRules()

			Collection<FactHandle> linkedItems = session.getFactHandles([accept: { obj ->
				return (obj instanceof LinkingItemFact && obj.linked) || (obj instanceof BankTransactionLinkFact)
			}] as ObjectFilter)

		then:
			linkedItems.size() == 3

		when:

			linkedItems.each {
				session.delete(it)1
			}

		then:
			true
	}

	static def CCY_LIST = ["EUR", "GBP", "USD"]
	static def MAX_AMOUNT = 1000

	def "performance"() {
		given:

			def count = 100000

			def inputData = []

			def rnd = new Random()
			(1..count).each { idx ->
				inputData << [currency: CCY_LIST[rnd.nextInt(CCY_LIST.size())], amount: rnd.nextInt(MAX_AMOUNT) + 1, reference: "${idx}"]
			}

		and:
			def session = container.newKieSession()
//			session.addEventListener(new DebugRuleRuntimeEventListener())


		and:
			inputData.each {
				def bt = new LinkingItemFact(source: LinkingItemSource.BANK_TRANSACTION, amount: -it.amount, currency: it.currency, reference: it.reference)
				session.insert(bt)

				def ei = new LinkingItemFact(source: LinkingItemSource.EXCHANGE_INSTRUCTION, amount: it.amount, currency: it.currency, reference: it.reference)
				session.insert(ei)
			}

		when:
			int rulesFired = session.fireAllRules()
			println(rulesFired + " Rules fired")

			Collection<FactHandle> linkedItems = session.getFactHandles([accept: { obj ->
				return (obj instanceof LinkingItemFact && obj.linked) || (obj instanceof BankTransactionLinkFact)
			}] as ObjectFilter)

		then:
			linkedItems.size() == 3 * count

		when:

			linkedItems.each {
				session.delete(it)
			}

		then:
			true
	}

	def "performance against application"() {
		given:

			def count = 100000

			def inputData = []

			def rnd = new Random()
			(1..count).each { idx ->
				inputData << [currency: CCY_LIST[rnd.nextInt(CCY_LIST.size())], amount: rnd.nextInt(MAX_AMOUNT) + 1, reference: "${idx}"]
			}

		and:
			def rest = new RESTClient( 'http://localhost:8080/' )

		and:
			inputData.each {
				rest.get(path: "insert/BANK_TRANSACTION/${it.currency}/-${it.amount}/${it.reference}")
				rest.get(path: "insert/EXCHANGE_INSTRUCTION/${it.currency}/${it.amount}/${it.reference}")
			}

		when:
//			def result = rest.get(path: "fireRules")
//			println("Rules fired: ${result.data}")

			def result = rest.get(path: "facts/all")
			println("All Facts: ${result.data}")
		then:
			true
	}

}
