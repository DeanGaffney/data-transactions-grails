package com.tran.data.validators

import com.tran.data.com.tran.data.validators.DateValidator
import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class DateValidatorSpec extends Specification implements GrailsUnitTest {

    DateValidator dateValidator

    def setup() {
        dateValidator = new DateValidator()
    }

    def cleanup() {
    }

    void "test date validator with valid date format"() {
        expect: "a valid format to be valid"
        dateValidator.getValidator() << "11-12-2018"
    }

    void "test date validator with invalid date format"() {
        expect: "an invalid format to be invalid"
        !(dateValidator.getValidator() << "11/12/2018")
    }
}