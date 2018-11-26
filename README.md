# data-transactions-grails
Simple REST service for data transactions built with Grails 3

# Config File Example
```code
transactions:
    file: C:/Users/dgaffney/Documents/dev/grails/data-transactions-grails/transactions.csv
```

## GET Endpoint
An example of a get request made for tha transactions
http://localhost:9000/transaction?type=credit&date=11-12-2018&limit=1
