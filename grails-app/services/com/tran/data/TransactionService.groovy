package com.tran.data

import com.tran.data.models.transaction.Transaction
import com.tran.data.models.transaction.TransactionFilter
import com.tran.data.models.transaction.TransactionFilterBuilder
import com.tran.data.models.transaction.TransactionQuery
import com.tran.data.models.transaction.TransactionResult
import com.tran.data.models.transaction.Transactions
import grails.gorm.transactions.Transactional

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import java.util.stream.Stream

@Transactional
class TransactionService {

    /**
     * Persists transactions to a file, this function is thread safe
     * due to interaction with the single transaction file
     *
     * @param transactions the transactions to persist
     * @return the transaction result object
     */
    synchronized TransactionResult persistTransactions(Transactions transactions) {
        File transactionFile = getTransactionsFile()

        String transactionsFileName = transactionFile.getName()
        File transactionCopyFile = getTransactionsCopyFile()

        TransactionResult transactionResult

        // write to the file and apply updates if needed
        transactionResult = updateAndWrite(transactionFile, transactionCopyFile, transactions)

        // delete the original transactions file and rename the copy to be the original file name
        if(!deleteTransactionsFile(transactionFile)){
            transactionResult.message = "Error occurred deleting"
        }

        if(!renameFile(transactionCopyFile, transactionsFileName)){
            transactionResult.message = "Error occurred renaming"
        }
        return transactionResult
    }

    /**
     * Writes transactions to the file, updates are carried out on transactions before
     * writing them out to the csv file
     *
     * @param transactionFile the transaction file to read from
     * @param transactionsCopyFile the transaction copy file to write to
     * @param transactions the transactions to update and write to the copy file
     * @return the transaction result
     */
    TransactionResult updateAndWrite(File transactionFile, File transactionsCopyFile, Transactions transactions){
        BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsCopyFile))
        // read from original transactions the file line by line to keep memory consumption down
        transactionFile.eachLine { String line ->
            if(!line.isEmpty()){
                // create a transaction from the line
                Transaction transactionFromFile = createTransactionFromLine(line)
                // with each line compare it against each transaction to see if there are duplicates
                transactions.entries.each { Transaction transaction ->
                    if(transaction == transactionFromFile){
                        // if the transactions are the same sum the transactions
                        transactionFromFile.sumTransactions(transaction)
                        transaction.existed = true
                    }
                }
                // write the transaction out to the copy file
                appendToTransactionsCopyFile(writer, transactionFromFile)
            }
        }
        // filter out existing transactions to get the new transactions and write them to the file
        List<Transaction> transactionsToCreate = filterExistingTransactions(transactions)
        transactionsToCreate.each { appendToTransactionsCopyFile(writer, it) }
        writer.close()
        return new TransactionResult(created: transactionsToCreate.size(), updated: transactions.entries.size() - transactionsToCreate.size(), message: "Transactions Stored")
    }

    /**
     * Gets a csv file from the users temp directory
     * with the supplied name
     *
     * @param name the name of the file to look for
     * @return the file
     */
    File getTempDirFile(String name){
        return new File(System.getProperty("java.io.tmpdir"), "${name}.csv")
    }
    /**
     * Creates a temp csv file which will be deleted
     * when the JVM is terminated. Should be used
     * for creating the transaction files
     *
     * @param name the name to use for the file
     * @return the temp file
     */
    File createTempCsvFile(String name){
        File tempCsv = getTempDirFile(name)
        tempCsv.createNewFile()
        tempCsv.setReadable(true)
        tempCsv.setWritable(true)
        return tempCsv
    }

    /**
     * Gets a temp csv file by checking to see if the file
     * already exists in the temp directory. If it does the file
     * is returned, otherwise the file is created as a temp file
     * marked for deletion and returned
     *
     * @param name the name of the file to check for
     * @return the temp csv file
     */
    File getTempCsvFile(String name){
        File file = getTempDirFile(name)
        if(!file.exists()){
            file = createTempCsvFile(name)
        }
        return file
    }

    /**
     * Gets the transaction file which is created
     * or retrieved depending on if it already exists
     *
     * @see #createTempCsvFile(java.lang.String)
     * @return the transaction file
     */
    File getTransactionsFile(){
        getTempCsvFile("transactions")
    }

    /**
     * Gets the transaction copy file which is created
     * or retrieved depending on if it already exists
     *
     * @see #createTempCsvFile(java.lang.String)
     * @return the transaction copy file
     */
    File getTransactionsCopyFile(){
        getTempCsvFile("transactions-copy")
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
        Path copyPath = Files.move(transactionFile.toPath(), transactionFile.toPath().resolveSibling(newName))
        return copyPath.toFile().exists()
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
        return transactions.entries.findAll{!it.existed}
    }

    /**
     * Append a transaction to the transaction copy file
     *
     * @param transactionsCopyFile the transaction copy file
     * @param transaction the transaction to append to the file
     */
    void appendToTransactionsCopyFile(BufferedWriter writer, Transaction transaction){
        writer.writeLine(transaction.toCsv())
    }

    /**
     * Gets transactions from the file using the transaction query object
     *
     * @param transactionQuery the query params to use as a filter
     * @return the transactions matching the params
     */
    synchronized Transactions getTransactions(TransactionQuery transactionQuery){
        Transactions transactions = new Transactions(entries: Collections.emptyList())
        File transactionFile = getTransactionsFile()
        // create the transaction filter
        TransactionFilter transactionFilter = new TransactionFilterBuilder()
                                                           .dateFilter(transactionQuery.date ?: ".*")   // if it's null match everything
                                                           .typeFilter(transactionQuery.type ?: ".*")   // if it's null match everything
                                                           .limit(transactionQuery.limit ?: 100)        // if no limit is supplied set default to 100
                                                           .build()

        // get all transactions limited to the supplied or default limit and matching the supplied filters
        transactions.entries = filterTransactions(transactionFile, transactionFilter)
        return transactions
    }

    /**
     * Filters transactions within the supplied transaction file using the
     * supplied transaction filter object and returns the transactions as a list
     *
     * @param transactionFile the transaction file to read from
     * @param transactionFilter the transaction filter to filter transactions
     * @return a list of filtered transactions form the transaction file
     */
    List<Transaction> filterTransactions(File transactionFile, TransactionFilter transactionFilter){
        Stream<String> streamLines = Files.lines(transactionFile.toPath())   // use stream here to lazily fetch data
                                          .limit(transactionFilter.limit)
        List<String> lines = streamLines.collect(Collectors.toList())
        try{
            streamLines.close()
        }catch(Exception e){
            e.printStackTrace()
        }

        return lines.collect {createTransactionFromLine(it)} // convert to transaction objects
                    .findAll{ it.date ==~ /${transactionFilter.dateFilter}/ }   // filter items
                    .findAll{ it.type ==~ /${transactionFilter.typeFilter}/}
    }

}
