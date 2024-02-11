package ktor.expos.security.token

/*
* This is a group of key-value pairs used to store information in the token
* */
data class TokenClaim(
    val name: String,
    val value: String
)
