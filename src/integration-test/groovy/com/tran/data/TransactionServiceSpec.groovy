package com.tran.data

import com.tran.data.models.transaction.Transaction
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
}
