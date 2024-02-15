/*
 * Copyright (c) 2024.
 * All rights reserved. Richard Uzor
 */

package ktor.expos.modules.transaction.models.requests

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Transaction(
    @BsonId var transactionID: String = ObjectId().toHexString(),
    val transactionFrom: String, //account sending the money
    val transactionTo: String, //account receiving the money
    val transactionAmount: Double,
    val transactionNarration: String,
    val transactionDate: String
    )
