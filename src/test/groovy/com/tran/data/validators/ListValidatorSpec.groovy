package com.tran.data.validators

import com.tran.data.com.tran.data.validators.ListValidator
import com.tran.data.models.transaction.Transaction
import com.tran.data.models.transaction.Transactions
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class ListValidatorSpec extends Specification implements GrailsUnitTest {

    ListValidator listValidator

    def setup() {
        listValidator = new ListValidator()
    }

    def cleanup() {
    }

    void "test validating valid transactions"() {
        setup: "transactions with valid transactions"
        Transactions transactions = new Transactions(entries: Arrays.asList(
                new Transaction(date: "11-12-2018", type: "credit", amount: "100.00")
        ))
        when: "I validate the transactions object"
        boolean isValid = transactions.validate()
        then: "it is valid"
        isValid
    }

    void "test validating invalid transactions"() {
        setup: "transactions with valid transactions"
        Transactions transactions = new Transactions(entries: Arrays.asList(
                new Transaction(date: "11/12/2018", type: "credit", amount: "100.00")
        ))
        when: "I validate the transactions object"
        boolean isValid = transactions.validate()
        then: "it is invalid"
        !isValid
    }
}