package com.tran.data.models.transaction

/**
 * Command object for the transaction GET request params
 * Created by dean on 25/11/18.
 */
class TransactionQuery {

    String date     // filter by transactions with a given date
    String type     // filter by transactions with a specific type
    int limit       // the number of transactions to return in the response

}
