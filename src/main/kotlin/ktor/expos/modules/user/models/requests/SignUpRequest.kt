package ktor.expos.modules.user.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val password: String,
)
@Serializable
data class SignInRequest(
    val username: String,
    val password: String,
)
