package com.tran.data.validators

import org.grails.testing.GrailsUnitTest
import spock.lang.Specification

class NumberValidatorSpec extends Specification implements GrailsUnitTest {

    NumberValidator numberValidator

    def setup() {
        numberValidator = new NumberValidator()
    }

    def cleanup() {
    }

    void "test validating a valid number"() {
        when: "I have a string which is a valid number"
        boolean isValid = numberValidator.getValidator() << "100.00"
        then: "it is valid"
        isValid
    }

    void "test validating an invalid number"() {
        when: "I have a string which is an invalid number"
        boolean isValid = numberValidator.getValidator() << "not a number"
        then: "it is invalid"
        !isValid
    }
}