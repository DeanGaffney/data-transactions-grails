package com.tran.data

import com.tran.data.models.transaction.Transaction
import grails.gorm.transactions.Transactional
import com.tran.data.models.transaction.Transactions
import java.io.File

@Transactional
class TransactionService {

    /**
     * Persists transactions to a file, this function is thread safe
     * due to interaction with the single transaction file
     *
     * @param transactions the transactions to persist
     * @return the task result object
     */
    def synchronized persistTransactions(Transactions transactions) {
        // TODO make this configurable with a config plugin

        // TODO make a task result object to return to the client
        File transactionFile = new File('/home/dean/dev/grails/data-transactions-grails/transactions.csv')
        String transactionsFileName = transactionFile.getName()
        File transactionCopyFile = new File(transactionFile.getParentFile(), "transactions-copy.csv")

        if(transactionFile.exists() && transactionCopyFile.createNewFile()){
            // write to the file and apply updates if needed
            updateAndWrite(transactionFile, transactionCopyFile, transactions)

            // delete the original transactions file
            deleteTransactionsFile(transactionFile)

            // rename the transactions-copy.csv file to be the original transactions file name
            renameFile(transactionCopyFile, transactionsFileName)

        }else{
            // file doesn't exist so no need to check for duplicate items, write all transactions to the file
            performInitialWrite(transactionFile, transactions)
        }
    }


    /**
     * Performs the initial write to the file
     *
     * @param transactionsFile the file to write to
     * @param transactions the transactions to write to the file
     */
    void performInitialWrite(File transactionsFile, Transactions transactions){
        transactionsFile.withWriterAppend { writer ->
            transactions.transactions.each { writer.println it.toCsv() }
        }
    }

    /**
     * Writes transactions to the file, updates are carried out on transactions before
     * writing them out to the csv file
     *
     * @param transactionFile the transaction file to read from
     * @param transactionsCopyFile the transaction copy file to write to
     * @param transactions the transactions to update and write to the copy file
     */
    void updateAndWrite(File transactionFile, File transactionsCopyFile, Transactions transactions){
        // read from original transactions the file line by line
        transactionFile.eachLine { String line ->
            // create a transaction from the line
            Transaction transactionFromFile = createTransactionFromLine(line)
            if(!line.isEmpty()){
                // with each line compare it against each transaction to see if there are duplicates
                transactions.transactions.each { Transaction transaction ->
                    //TODO need to remove transactions as they match, because the newest transactions aren't being written to the file
                    if(transaction == transactionFromFile){
                        // if the transactions are the same sum the transactions
                        transactionFromFile.sumTransactions(transactionFromFile)
                    }
                }
            }
            // write the transaction out to the copy file
            transactionsCopyFile.append(transactionFromFile.toCsv() + System.lineSeparator())
        }
    }

    /**
     * Deletes a transaction file
     *
     * @param transactionFile the file to delete
     * @return true if the file got deleted, false otherwise
     */
    boolean deleteTransactionsFile(File transactionFile){
        return transactionFile.delete()
    }

    /**
     * Renames the given file with the supplied name
     *
     * @param transactionFile the file to rename
     * @param newName the new name for the file
     * @return true if the file was renamed, false otherwise
     */
    boolean renameFile(File transactionFile, String newName){
        return transactionFile.renameTo(newName)
    }

    /**
     * Creates a transaction object from a line in the transaction file
     *
     * @param line the line to use for creating the transaction object
     * @return the transaction object created from the line
     */
    Transaction createTransactionFromLine(String line){
        String[] attributes = line.split(",")
        return new Transaction(date: attributes[0], type: attributes[1], amount: attributes[2])
    }

}
