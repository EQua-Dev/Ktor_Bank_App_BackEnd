package ktor.expos.security.token

/*
* A data class that summarizes all the fields we want to have for our JWT token
* */
data class TokenConfig(
    val issuer: String,
    val audience: String,
    val expiresIn: Long,
    val secret: String,
)
