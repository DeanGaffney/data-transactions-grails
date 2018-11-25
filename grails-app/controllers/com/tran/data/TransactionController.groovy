package com.tran.data


import com.tran.data.models.transaction.Transaction
import com.tran.data.models.transaction.TransactionQuery
import com.tran.data.models.transaction.Transactions
import grails.rest.*

class TransactionController extends RestfulController {

    static responseFormats = ['json']

    def transactionService

    TransactionController() {
        super(Transactions)
    }

    /**
     * Uses a transaction query object to filter the data returned
     *
     * @param transactionQuery the transaction query object to use for building filters
     * @return transactions matching the transactionQuery params
     */
    def index(TransactionQuery transactionQuery) {
        respond transactionService.getTransactions(transactionQuery)
    }

    /**
     * Takes in a Transactions objects and stores them to the transactions file
     *
     * @param transactions the transactions to store
     * @return a task result object
     */
    def save(Transactions transactions){
        if(transactions.hasErrors()){
            respond transactions.errors
        }else {
            transactionService.persistTransactions(transactions)
            respond transactions
        }
    }

}
