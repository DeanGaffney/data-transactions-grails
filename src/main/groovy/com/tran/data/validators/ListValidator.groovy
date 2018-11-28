package com.tran.data.validators

import grails.validation.Validateable

/**
 * A validators for validating that a list of
 * POGOs are valid
 * Created by dean on 24/11/18.
 */
class ListValidator implements Validator {

    /**
     * Returns a validators which makes sure each
     * validateable item in a list is valid
     *
     * @return the validators
     */
    @Override
    Closure<Boolean> getValidator() {
        return { List<Validateable> values -> values.every{ it.validate() } }
    }
}
