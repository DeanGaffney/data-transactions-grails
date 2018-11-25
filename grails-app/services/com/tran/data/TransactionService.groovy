package com.tran.data

import grails.gorm.transactions.Transactional

import java.nio.file.Files
import java.util.stream.Collectors

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
            if(!line.isEmpty()){
                // create a transaction from the line
                Transaction transactionFromFile = createTransactionFromLine(line)
                // with each line compare it against each transaction to see if there are duplicates
                transactions.transactions.each { Transaction transaction ->
                    if(transaction == transactionFromFile){
                        // if the transactions are the same sum the transactions
                        transactionFromFile.sumTransactions(transaction)
                        transaction.existed = true
                    }
                }
                // write the transaction out to the copy file
                appendToTransactionsCopyFile(transactionsCopyFile, transactionFromFile)
            }
        }
        // filter out existing transactions to get the new transactions and write them to the file
        filterExistingTransactions(transactions).each { appendToTransactionsCopyFile(transactionsCopyFile, it) }
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


    /**
     * Gets all the transactions which did not previously exist
     *
     * @param transactions the transaction object to filter from
     * @return a list of transactions which can be considered new transactions
     */
    List<Transaction> filterExistingTransactions(Transactions transactions){
        return transactions.transactions.findAll{!it.existed}
    }

    /**
     * Append a transaction to the transaction copy file
     *
     * @param transactionsCopyFile the transaction copy file
     * @param transaction the transaction to append to the file
     */
    void appendToTransactionsCopyFile(File transactionsCopyFile, Transaction transaction){
        transactionsCopyFile.append(transaction.toCsv() + System.lineSeparator())
    }


    /**
     * Gets transactions from the file using the transaction query object
     *
     * @param transactionQuery the query params to use as a filter
     * @return the transactions matching the params
     */
    Transactions getTransactions(TransactionQuery transactionQuery){
        Transactions transactions = new Transactions(transactions: Collections.emptyList())
        File transactionFile = new File('/home/dean/dev/grails/data-transactions-grails/transactions.csv')
        if(transactionFile.exists()){
            // create the transaction filter
            TransactionFilter transactionFilter = new TransactionFilterBuilder()
                                                               .dateFilter(transactionQuery.date ?: ".*")   // if it's null match everything
                                                               .typeFilter(transactionQuery.type ?: ".*")   // if it's null match everything
                                                               .build()

            // get all transactions limited to the supplied or default limit and matching the supplied filters
            transactions.transactions = Files.lines(transactionFile.toPath())
                                             .limit(transactionQuery.limit ?: 100)
                                             .collect(Collectors.toList())
                                             .collect {createTransactionFromLine(it)}
                                             .findAll{ it.date ==~ /${transactionFilter.dateFilter}/ }
                                             .findAll{ it.type ==~ /${transactionFilter.typeFilter}/}
        }
        return transactions
    }

}
