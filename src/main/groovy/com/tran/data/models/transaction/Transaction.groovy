package com.tran.data.models.transaction

import com.tran.data.com.tran.data.validators.ValidatorFactory
import com.tran.data.com.tran.data.validators.ValidatorType
import grails.validation.Validateable

/**
 * POGO to be used as a command object for the rest endpoints
 * Created by dean on 24/11/18.
 */
class Transaction implements  Validateable {

    String date
    String type
    String amount

    static constraints = {
        date(nullable: false, blank: false, validator: ValidatorFactory.getValidator(ValidatorType.DATE))
        type(nullable: false, blank: false)
        amount(nullable: false, blank: false, validator: ValidatorFactory.getValidator(ValidatorType.NUMBER))
    }

}
