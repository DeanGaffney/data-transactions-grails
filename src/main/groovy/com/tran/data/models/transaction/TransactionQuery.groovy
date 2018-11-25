package com.tran.data.models.transaction

import grails.validation.Validateable

/**
 * Command object for the transaction GET request params
 * Created by dean on 25/11/18.
 */
class TransactionQuery implements Validateable {

    String date     // filter by transactions with a given date
    String type     // filter by transactions with a specific type
    int limit       // the number of transactions to return in the response

    static constraints = {
        date(nullable: true)
        type(nullable: true)
        limit(nullable: true)
    }
}
