package com.tran.data.models.transaction

import com.tran.data.com.tran.data.validators.ValidatorFactory
import com.tran.data.com.tran.data.validators.ValidatorType
import grails.validation.Validateable

/**
 * POGO to be used as a command object for the rest endpoints
 * Created by dean on 24/11/18.
 */
class Transaction implements Validateable, Comparable<Transaction> {

    String date
    String type
    String amount

    static constraints = {
        date(nullable: false, blank: false, validator: ValidatorFactory.getValidator(ValidatorType.DATE))
        type(nullable: false, blank: false)
        amount(nullable: false, blank: false, validator: ValidatorFactory.getValidator(ValidatorType.NUMBER))
    }

    void sumTransactions(Transaction transaction){
        float sum = Float.parseFloat(this.amount) + Float.parseFloat(transaction.amount)
        this.amount = String.format("%.2f", sum)
    }

    /**
     * Converts a transaction to csv format
     *
     * @return a csv representation of the transaction object
     */
    String toCsv(){
        return "$date,$type,$amount"
    }

    /**
     * Compares two transactions based on their date and type
     * @param transaction the transaction to compare to
     * @return the compareTo integer
     */
    @Override
    int compareTo(Transaction transaction) {
        return this.date <=> transaction.date ?: this.type <=> transaction.type
    }

}
