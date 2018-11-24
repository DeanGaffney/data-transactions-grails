package com.tran.data.com.tran.data.validators

/**
 * Created by dean on 24/11/18.
 */
interface Validator {

    /**
     * Gets a validator
     * @return a closure representing the validator
     */
    Closure<Boolean> getValidator()
}