/*
 * Copyright (c) 2024.
 * All rights reserved. Richard Uzor
 */

package ktor.expos.modules.bank.models.responses

import kotlinx.serialization.Serializable
import ktor.expos.modules.bank.models.requests.BankCommission

@Serializable
data class BankCommissionResponse(
    val totalAmount: Double,
    val commissions: List<BankCommission>
)
