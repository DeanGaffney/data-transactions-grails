package com.tran.data

import com.tran.data.models.Transaction
import grails.rest.*

class TransactionController extends RestfulController {

    static responseFormats = ['json', 'xml']

    TransactionController() {
        super(Transaction)
    }

    def index(){
        respond new Transaction(date: new Date().toString(), type: "some type", amount: "123.45")
    }

    def save(Transaction transaction){
        if(transaction.hasErrors()){
            respond transaction.errors
        }else{
            respond transaction
        }
    }

}
