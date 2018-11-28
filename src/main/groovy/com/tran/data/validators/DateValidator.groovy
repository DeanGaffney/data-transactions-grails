package com.tran.data.validators

import java.text.ParseException

/**
 * Validator utility class for
 * supplying validators for POGOs
 * Created by dean on 24/11/18.
 */
class DateValidator implements Validator{

    // the date format to use for validating dates
    private static String VALID_DATE_FORMAT = "dd-MM-yyyy"

    /**
     * Date validators that makes sure a string is a valid date
     * that is formatted to the expected date format
     *
     * @param date the string representation of a date to validate
     * @return true if the date is valid, false otherwise
     */
    @Override
    Closure<Boolean> getValidator() {
        return { String date ->
            boolean isValid = true
            try {
                new Date().parse(VALID_DATE_FORMAT, date)
            }catch (ParseException e){
                isValid = false
            }
            return isValid
        }
    }
}
