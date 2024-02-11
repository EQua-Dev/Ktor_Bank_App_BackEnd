package ktor.expos.security.token

import ktor.expos.security.token.TokenClaim
import ktor.expos.security.token.TokenConfig

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim,
    ): String
}