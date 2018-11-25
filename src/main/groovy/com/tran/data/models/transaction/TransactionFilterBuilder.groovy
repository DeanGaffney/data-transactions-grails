package com.tran.data.models.transaction

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

/**
 * Created by dean on 25/11/18.
 */
@Builder(builderStrategy = ExternalStrategy, forClass= TransactionFilter)
class TransactionFilterBuilder {}
