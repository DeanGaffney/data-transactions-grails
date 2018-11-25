package com.tran.data


import com.tran.data.models.transaction.Transaction
import com.tran.data.models.transaction.Transactions
import grails.rest.*

class TransactionController extends RestfulController {

    static responseFormats = ['json', 'xml']

    def transactionService

    TransactionController() {
        super(Transactions)
    }

    def index(){
        respond new Transaction(date: new Date().toString(), type: "some type", amount: "123.45")
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
