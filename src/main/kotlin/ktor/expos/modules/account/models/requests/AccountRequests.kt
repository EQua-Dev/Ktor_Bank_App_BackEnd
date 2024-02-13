package ktor.expos.modules.account.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    //val userId: String,
    val accountType: String
)