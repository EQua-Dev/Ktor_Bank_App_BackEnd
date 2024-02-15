/*
 * Copyright (c) 2024.
 * All rights reserved. Richard Uzor
 */

package ktor.expos.modules.bank.models.requests

data class BankCommission(
    val commissionID: String,
    val dateCreated: String,
    val amount: Double,
    val transactionID: String
)
