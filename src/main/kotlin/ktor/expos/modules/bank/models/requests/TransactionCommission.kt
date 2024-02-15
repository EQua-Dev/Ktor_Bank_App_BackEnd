/*
 * Copyright (c) 2024.
 * All rights reserved. Richard Uzor
 */

package ktor.expos.modules.bank.models.requests

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class BankCommission(
    @BsonId var commissionID: String = ObjectId().toHexString(),
    val dateCreated: String,
    val amount: Double,
    val transactionID: String
)
