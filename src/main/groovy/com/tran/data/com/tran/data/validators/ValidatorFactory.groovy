package com.tran.data.com.tran.data.validators

/**
 * A validator factory which deals with
 * returning the correct validator for the validator type
 *
 * Created by dean on 24/11/18.
 */
class ValidatorFactory {

    /**
     * Gets a validator closure for the supplied validator type
     *
     * @param type the type of validator to get
     * @return the validator closure
     */
    static Closure<Boolean> getValidator(ValidatorType type){
        // in the event a type is not matched return false and make the validation fail
        Closure<Boolean> validator = { Object value -> return false }
        if(type == ValidatorType.DATE){
            validator = new DateValidator().getValidator()
        }else if(type == ValidatorType.NUMBER){
            validator = new NumberValidator().getValidator()
        }else if(type == ValidatorType.LIST){
            validator = new ListValidator().getValidator()
        }
        return validator
    }
}
