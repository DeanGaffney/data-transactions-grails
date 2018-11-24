package com.tran.data.com.tran.data.validators

/**
 * A number validator for POGOs
 * Created by dean on 24/11/18.
 */
class NumberValidator implements Validator{

    /**
     * Gets the number validator
     * this validator returns true if a given string is a number
     * @return the validator
     */
    @Override
    Closure<Boolean> getValidator() {
        return { String value -> value.isNumber() }
    }
}
