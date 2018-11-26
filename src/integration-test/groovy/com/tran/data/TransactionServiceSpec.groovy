package com.tran.data

import com.tran.data.models.transaction.Transaction
import com.tran.data.models.transaction.TransactionFilterBuilder
import com.tran.data.models.transaction.TransactionFilter
import com.tran.data.models.transaction.TransactionQuery
import com.tran.data.models.transaction.TransactionResult
import com.tran.data.models.transaction.Transactions
import grails.testing.mixin.integration.Integration
import grails.transaction.*
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.*

@Integration
@Rollback
class TransactionServiceSpec extends Specification {

    @Autowired
    TransactionService transactionService

    String testFileName

    def setup() {
        testFileName = "test"
    }

    def cleanup() {
        transactionService.deleteTransactionsFile(transactionService.getTransactionsFile())
        transactionService.deleteTransactionsFile(transactionService.getTransactionsCopyFile())
        transactionService.deleteTransactionsFile(transactionService.getTempCsvFile(testFileName))
    }

    void "test creating new transactions"(){
        setup:
        Transactions transactions = new Transactions(entries: Arrays.asList(
                new Transaction(date: "11-12-2018", type: "credit", amount: "340.43"),
                new Transaction(date: "11-12-2017", type: "credit", amount: "340.43")
        ))
        when: "I save transactions"
        TransactionResult result = transactionService.persistTransactions(transactions)
        then:"there was 2 transactions created"
        result.created == 2
        result.updated == 0
    }

    void "test updating existing transactions"(){
        setup:
        Transactions transactions = new Transactions(entries: Arrays.asList(
                new Transaction(date: "11-12-2018", type: "credit", amount: "340.43"),
                new Transaction(date: "11-12-2017", type: "credit", amount: "340.43")
        ))
        when: "I save transactions twice"
        TransactionResult result
        2.times {result = transactionService.persistTransactions(transactions)}
        then:"there was 2 transactions updated"
        result.created == 0
        result.updated == 2
        and:
        when: "I get the updated transactions"
        Transactions retrievedTransactions = transactionService.getTransactions(new TransactionQuery())
        then: "the amounts have been summed together"
        retrievedTransactions.entries.every {it.amount == String.format("%.2f", Float.parseFloat(transactions.entries[0].amount) * 2)}
    }

    void "test creating temp file"() {
        when: "I create a temp csv file"
        File tempFile = transactionService.createTempCsvFile(testFileName)
        then: "it exists"
        tempFile.exists()
    }

    void "test getting temp file"(){
        expect: "temp file to exist"
        transactionService.getTempCsvFile(testFileName).exists()
    }

    void "test deleting a transaction file"(){
        when: "I get a transaction file"
        File file = transactionService.getTransactionsFile()
        then: "it exists"
        file.exists()
        and:"when i delete it"
        boolean deleted = transactionService.deleteTransactionsFile(file)
        then: "it is deleted"
        deleted
    }

    void "test renaming transaction file"(){
        when: "I get a transaction file"
        File file = transactionService.getTransactionsFile()
        String originalName = file.getName()
        File copyFile = transactionService.getTransactionsCopyFile()
        then: "it exists"
        file.exists()
        copyFile.exists()
        and: "i delete the original"
        transactionService.deleteTransactionsFile(file)
        and: "when i rename it"
        boolean renamed = transactionService.renameFile(copyFile, originalName)
        then: "it was renamed"
        renamed
    }

    void "test creating transaction from a line"(){
        when: "I have a line that represents a transaction"
        String csvLine = "11-12-2018,credit,200.35"
        and: "I convert it to a transaction object"
        Transaction transaction = transactionService.createTransactionFromLine(csvLine)
        then: "the transaction was created"
        transaction.toCsv() == csvLine
    }

    void "test filtering existing transactions"(){
        setup: "a transactions object"
        Transactions transactions = new Transactions(entries: Arrays.asList(
                new Transaction(date: "11-12-2018", type: "credit", amount: "340.43", existed: true),
                new Transaction(date: "11-12-2017", type: "credit", amount: "340.43", existed: false)
        ))
        when: "I filter out existing transactions"
        List<Transaction> entries = transactionService.filterExistingTransactions(transactions)
        then: "there is only one transaction remaining"
        entries.size() == 1
    }

    void "test appending to copy file"(){
        setup:
        BufferedWriter writer = new BufferedWriter(new FileWriter(transactionService.getTransactionsCopyFile()))
        Transaction transaction = new Transaction(date: "11-12-2018", type: "credit", amount: "123.123")
        when: "I append a transaction twice to a file"
        2.times { transactionService.appendToTransactionsCopyFile(writer, transaction) }
        and: "I close the writer"
        writer.close()
        then: "the file contains 2 lines"
        transactionService.getTransactionsCopyFile().readLines().size() == 2
    }

    void "test getting transactions"(){
        setup:
        Transactions transactions = new Transactions(entries: Arrays.asList(
                new Transaction(date: "11-12-2018", type: "credit", amount: "340.43"),
                new Transaction(date: "11-12-2017", type: "credit", amount: "340.43")
        ))
        when: "I save transactions"
        transactionService.persistTransactions(transactions)
        and: "I make get them"
        Transactions retrievedTransactions = transactionService.getTransactions(new TransactionQuery())
        then: "there are 2 transactions returned"
        retrievedTransactions.entries.size() == 2
    }

    void "test filtering transactions"(){
        setup:
        Transactions transactions = new Transactions(entries: Arrays.asList(
                new Transaction(date: "11-12-2018", type: "credit", amount: "340.43"),
                new Transaction(date: "11-12-2017", type: "tax", amount: "340.43")
        ))
        TransactionQuery queryfilter = new TransactionQuery(date: "11-12-2018")
        when: "I save transactions"
        transactionService.persistTransactions(transactions)
        and: "I filter the transactions by date"
        List<Transaction> entries = transactionService.getTransactions(queryfilter).entries
        then: "1 entry is returned"
        entries.size() == 1
        when: "I filter by type"
        queryfilter = new TransactionQuery(type: "credit")
        entries = transactionService.getTransactions(queryfilter).entries
        then: "1 entry is returned"
        entries.size() == 1
        when: "I limit he size on the filter"
        queryfilter = new TransactionQuery(limit: 1)
        entries = transactionService.getTransactions(queryfilter).entries
        then: "1 entry is returned"
        entries.size() == 1
        when: "I dont specify any query params"
        queryfilter = new TransactionQuery()
        and: "I get the transactions"
        entries = transactionService.getTransactions(queryfilter).entries
        then: "there are 2 transactions"
        entries.size() == 2
    }
}