package com.tran.data

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

/**
 * Created by dean on 25/11/18.
 */
@Builder(builderStrategy = ExternalStrategy, forClass= TransactionFilter)
class TransactionFilterBuilder {}
