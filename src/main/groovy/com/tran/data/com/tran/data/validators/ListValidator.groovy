package com.tran.data.com.tran.data.validators

import grails.validation.Validateable

/**
 * A validator for validating that a list of
 * POGOs are valid
 * Created by dean on 24/11/18.
 */
class ListValidator implements Validator {

    /**
     * Returns a validator which makes sure each
     * validateable item in a list is valid
     *
     * @return the validator
     */
    @Override
    Closure<Boolean> getValidator() {
        return { List<Validateable> values -> values.every{ it.validate() } }
    }
}
