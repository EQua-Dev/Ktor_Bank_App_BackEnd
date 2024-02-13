package ktor.expos.modules.account.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class AccountDummyResponse(
    val userId: String,
    val accountNumber: String,
    val accountBalance: String
)
