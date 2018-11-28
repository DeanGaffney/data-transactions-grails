package com.tran.data.models.transaction

import grails.validation.Validateable

/**
 * Command object for the transaction GET request params
 * Created by dean on 25/11/18.
 */
class TransactionQuery implements Validateable {

    // constant for setting a default max limit on entries returned
    public static final int DEFAULT_MAX_LIMIT = 100

    // constant for matching everything with a regex
    public static final String DEFAULT_MATCH_ALL = ".*"

    String date = DEFAULT_MATCH_ALL     // filter by transactions with a given date
    String type = DEFAULT_MATCH_ALL     // filter by transactions with a specific type
    int limit = DEFAULT_MAX_LIMIT       // the number of transactions to return in the response

    static constraints = {
        date(nullable: true)
        type(nullable: true)
        limit(nullable: true)
    }
}
