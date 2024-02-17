/*
 * Copyright (c) 2024.
 * All rights reserved. Richard Uzor
 */

package ktor.expos.modules.transaction.models.responses

import kotlinx.serialization.Serializable
import ktor.expos.modules.account.models.responses.AccountData
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class TransactionResponse(
    val transactionID: String,
    var transactionTime: String,
    val transactionAmount: Double,
    val transactionNarration: String,
    val transactionReceiver: AccountData,
    val transactionSender: AccountData
)
