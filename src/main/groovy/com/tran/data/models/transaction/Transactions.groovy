package com.tran.data.models.transaction

import com.tran.data.validators.ValidatorFactory
import com.tran.data.validators.ValidatorType
import grails.validation.Validateable

/**
 * Class is a wrapper for a list of
 * Transaction objects
 *
 * Created by dean on 24/11/18.
 */
class Transactions implements  Validateable {

    List<Transaction> entries

    static constraints ={
        entries(nullable: false, validator: ValidatorFactory.getValidator(ValidatorType.LIST))
    }
}
