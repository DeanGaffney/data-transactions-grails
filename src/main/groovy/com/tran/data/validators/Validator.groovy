package com.tran.data.validators

/**
 * Created by dean on 24/11/18.
 */
interface Validator {

    /**
     * Gets a validators
     * @return a closure representing the validators
     */
    Closure<Boolean> getValidator()
}