package com.tran.data

import com.tran.data.models.transaction.Transaction
import grails.gorm.transactions.Transactional
import com.tran.data.models.transaction.Transactions
import java.io.File

@Transactional
class TransactionService {

    def handleTransactions(Transactions transactions) {
        // TODO make this configurable with a config plugin
        File transactionFile = new File('/home/dev/grails/data-transactions-grails/transactions.csv')

        if(transactionFile.exists()){
            checkForDuplicateTransactions(transactionFile, transactions)
            // delete the transactions.csv file
            deleteTransactionsFile(transactionFile)
            // rename the transactions-copy.csv file to be transactions.csv
            renameTransactionCopyFile(transactionFile)
        }else{
            // file doesn't exist so no need to check for duplicate items
            // write all transactions to the file
        }
    }

    def checkForDuplicateTransactions(File transactionFile, Transactions transactions){
        // read from the file line by line
        transactionFile.eachLine { String line ->
            // with each line compare it against each transaction to see if there are duplicates
            transactions.each { transaction ->
                if(transaction)
            }
            // if there are duplicates take the line and sum the amount
            // write the line to a separate file 'transactions-copy.csv'
        }
    }

    /**
     * Deletes a file
     * @param transactionFile the file to delete
     * @return true if the file got deleted, false otherwise
     */
    boolean deleteTransactionsFile(File transactionFile){
        // delete the file here
    }

    boolean renameTransactionCopyFile(File transactionFile){
        // rename the file
        // return file.renameTo( new File('c:/temp/new_name.txt') )
    }

    Transaction createTransactionFromLine(String line){
        String[] attributes = line.split(",")
        return new Transaction(date: attributes[0], type: attributes[1], amount: attributes[2])
    }

}
