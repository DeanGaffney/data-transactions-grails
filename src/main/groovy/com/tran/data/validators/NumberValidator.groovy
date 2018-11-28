package com.tran.data.validators

/**
 * A number validators for POGOs
 * Created by dean on 24/11/18.
 */
class NumberValidator implements Validator{

    /**
     * Gets the number validators
     * this validators returns true if a given string is a number
     * @return the validators
     */
    @Override
    Closure<Boolean> getValidator() {
        return { String value -> value.isNumber() }
    }
}
