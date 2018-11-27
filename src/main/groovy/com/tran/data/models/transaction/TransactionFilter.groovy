package com.tran.data.models.transaction

/**
 * An object representing a filter to use
 * when searching for transactions
 *
 * Created by dean on 25/11/18.
 */
class TransactionFilter {
    // constant for setting a default max limit on entries returned
    public static final int DEFAULT_MAX_LIMIT = 100

    String dateFilter
    String typeFilter
    int limit
}
