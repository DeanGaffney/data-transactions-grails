package com.tran.data.models

import com.tran.data.models.transaction.Transaction
import com.tran.data.models.transaction.TransactionQuery
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class TransactionSpec extends Specification implements GrailsUnitTest {

    Transaction transaction
    String transactionDate
    String transactionType
    String transactionAmount

    def setup() {
        transactionDate = "11-12-2018"
        transactionType = "credit"
        transactionAmount = "120.45"
        transaction = new Transaction(date: transactionDate, type: transactionType, amount: transactionAmount)
    }

    def cleanup() {
    }

    void "test compare to"() {
        setup: "a transaction which will match another transaction"
        Transaction otherTransaction = new Transaction(date: transactionDate, type: transactionType, amount: transactionAmount)

        when: "both transactions have the same date and type"
        transaction.date == otherTransaction.date && transaction.type == otherTransaction.type

        then: "they are considered to be equal"
        transaction == otherTransaction

        when: "the amount is differs between both transactions"
        otherTransaction.amount = "12.45"

        then: "they are still considered equal"
        transaction == otherTransaction

        when: "the date differs between both transactions but the type is the same"
        otherTransaction.amount = transactionAmount
        otherTransaction.date = "11-12-2017"

        then: "they are not considered equal"
        transaction != otherTransaction

        when: "the type differs between both transactions but the date is the same"
        otherTransaction.date = transactionDate
        otherTransaction.type = "different type"

        then: "they are not considered to be equal"
        transaction != otherTransaction

        when: "both the date and type differ between transactions"
        otherTransaction.date = "11-12-2017"
        otherTransaction.type = "different type"

        then: "they are not considered to be equal"
        transaction != otherTransaction
    }


    void "test to csv"(){
        transaction.toCsv() == "$transactionDate,$transactionType,$transactionAmount"
    }

    void "test sum transactions"(){
        setup:
        Transaction otherTransaction = new Transaction(date: transactionDate, type: transactionType, amount: transactionAmount)
        println transaction.amount

        when: "I sum the two transactions together"
        transaction.sumTransactions(otherTransaction)

        then: "the transaction has the the sum of both transactions"
        transaction.amount == String.format("%.2f", Float.parseFloat(transactionAmount) + Float.parseFloat(transactionAmount))

        and: "the transaction amount is formatted to 2 decimal places"
        transaction.amount ==~ /^\d+\.\d{2}$/
    }

    void "test valid transaction"(){
        expect:
        transaction.validate()
    }

    void "test invalid transaction"(){
        setup:
        Transaction invalidTransaction = new Transaction(date: "11/12/2018", type: "credit", amount: "120.00")
        when: "I validate a transaction with the wrong date format"
        boolean isValid = invalidTransaction.validate()
        then: "it is not valid"
        !isValid
        when: "I validate a transaction with an amount that can't be parsed to a number"
        invalidTransaction.date = transaction.date
        invalidTransaction.amount = "not a number"
        then: "it is invalid"
        !invalidTransaction.validate()
        when: "I make the attributes empty"
        invalidTransaction.amount = ""
        invalidTransaction.date = ""
        invalidTransaction.type = ""
        then: "it is invalid"
        !invalidTransaction.validate()
        when: "i set all values to null"
        invalidTransaction.amount = null
        invalidTransaction.date = null
        invalidTransaction.type = null
        then:
        !invalidTransaction.validate()
    }
}