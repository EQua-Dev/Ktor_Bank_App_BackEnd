package ktor.expos.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountRequest(
    //val userId: String,
    val accountType: String
)