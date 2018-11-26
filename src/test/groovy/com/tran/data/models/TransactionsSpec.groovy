package com.tran.data.models

import com.tran.data.models.transaction.Transaction
import com.tran.data.models.transaction.Transactions
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class TransactionsSpec extends Specification implements GrailsUnitTest {

    def setup() {
    }

    def cleanup() {
    }

    void "test validation on valid transactions object"() {
        setup: "a valid transactions object with valid entries"
        Transactions transactions = new Transactions(entries: Arrays.asList(
                new Transaction(date: "11-12-2018", type: "credit", amount: "340.43"),
                new Transaction(date: "11-12-2017", type: "tax", amount: "340.43")
        ))
        expect: "it to be valid"
        transactions.validate()
    }

    void "test validation on invalid transactions object"() {
        setup: "an invalid transactions object with invalid entries"
        Transactions transactions = new Transactions(entries: Arrays.asList(
                new Transaction(date: "11-12-2018", type: "credit", amount: "340.43"),
                new Transaction(date: "11/12/2017", type: "tax", amount: "340.43")
        ))
        expect: "it to be invalid"
        !transactions.validate()
    }
}