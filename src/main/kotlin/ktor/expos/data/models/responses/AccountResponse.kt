package ktor.expos.data.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class AccountDummyResponse(
    val userId: String,
    val accountNumber: String,
    val accountBalance: String
)
