# data-transactions-grails
Simple REST service for transaction data built with Grails 3

## Prerequisites
* Grails 3.3.x
* Groovy 2.4.11
* Java 8 (v 1.8.0_161)

## POST endpoint
You can send transactions to the web service by supplying the data in this format:
```json
{
    "entries": [
        {
            "date": "11-12-2018",
            "type": "credit",
            "amount": "200.45"
        },
        {
            "date": "11-12-2017",
            "type": "tax",
            "amount": "145.70"
        }
    ]
}
```
* Post the data to the following endpoint http://localhost/transaction
* Each entry in the json array will written to a transactions.csv file which is stored in the users temp directory.
* Before being written to a file each entry is compared against each line in the existing transactions file. If a line has the same date and type as an incoming entry they are considered equal and their amount values are summed together and the updated transaction is written to the file.
* When checking for duplicate entries a copy of the transactions file is made and as duplicates and new transactions are found they are written to this copy file.
* Once all transactions are dealt with the old transactions file is deleted and the copy is renamed to the original file name.
* To keep memory consumption low all IO is done line by line.
* Any file IO is synchronized to stop several threads writing or reading the same transaction file and corrupting the data.
* Once the data is inserted a response is returned to the client in the following format
  ```json
  {
      "created": 10,    // the number of new transactions
      "updated": 3,     // the number of updated transactions
      "message": "Some message"
  }
  ```
### Data Validation
Validators were created for the data so please ensure that your entries in the transaction data follow the following rules:
* Date must be in the format "dd-MM-yyyy"
* Amount must be a valid number

If your entries do not follow this schema the POST request will be rejected.

## GET Endpoint
You can perform filtered searches on the GET endpoint for transactions. Below are some examples of different queries against the endpoint. The data comes back in the following format:
```json
{
    "entries": [
        {
            "date": "11-12-2018",
            "type": "credit",
            "amount": "200.45"
        },
        {
            "date": "11-12-2017",
            "type": "tax",
            "amount": "145.70"
        }
    ]
}
```

### Filter by Date
The following request will get back all transactions matching the date in the query params:
http://localhost:9000/transaction?date=11-12-2018

### Filter by Type
The following request will get back all transactions containing the type supplied in the query params:
http://localhost:9000/transaction?type=credit

### Filter by Date & Type
The following request will get back all transactions that match the date **AND** the type supplied with the query params.

### Limit Results
To limit the number of entries returned you can add a limit query param to the request. The following request will limit the transactions returned to 1:
http://localhost:9000/transaction?type=credit&date=11-12-2018&limit=1

### Get All Transactions
You can get back all transactions by leaving out the query params from the request. Please note the max entries returned is 100 entries unless you specify a limit in the query params. The request would like like the following:
http://localhost:9000/transaction

## Tests
All tests passing

# Authors
Dean Gaffney