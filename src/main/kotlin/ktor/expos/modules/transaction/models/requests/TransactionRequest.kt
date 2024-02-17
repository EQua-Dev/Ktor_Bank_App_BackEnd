/*
 * Copyright (c) 2024.
 * All rights reserved. Richard Uzor
 */

package ktor.expos.modules.transaction.models.requests

import kotlinx.serialization.Serializable
import ktor.expos.modules.account.models.responses.AccountData
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

@Serializable
data class Transaction(
    @BsonId var transactionID: String = ObjectId().toHexString(),
    @field:BsonProperty(useDiscriminator = true) val transactionFrom: AccountData, //account sending the money
    @field:BsonProperty(useDiscriminator = true) val transactionTo: AccountData?, //account receiving the money
    val transactionAmount: Double,
    val transactionNarration: String,
    val transactionDate: String
    )

@Serializable
data class TransactionRequest(
    val transactionTo: String, //account receiving the money
    val transactionAmount: Double,
    val transactionNarration: String,
    val transactionDate: String
    )
