package com.tran.data.com.tran.data.validators

/**
 * A validator factory which deals with
 * returning the correct validator for the validator type
 * Created by dean on 24/11/18.
 */
class ValidatorFactory {

    /**
     * Gets a validator for the supplied validator type
     *
     * @param type the type of validator to get
     * @return the validator
     */
    static Closure<Boolean> getValidator(ValidatorType type){
        Closure<Boolean> validator = null
        if(type == ValidatorType.DATE){
            validator = new DateValidator().getValidator();
        }else if(type == ValidatorType.NUMBER){
            validator = new NumberValidator().getValidator()
        }
        return validator
    }
}
