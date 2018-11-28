package com.tran.data.validators

/**
 * A validators factory which deals with
 * returning the correct validators for the validators type
 *
 * Created by dean on 24/11/18.
 */
class ValidatorFactory {

    /**
     * Gets a validators closure for the supplied validators type
     *
     * @param type the type of validators to get
     * @return the validators closure
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
