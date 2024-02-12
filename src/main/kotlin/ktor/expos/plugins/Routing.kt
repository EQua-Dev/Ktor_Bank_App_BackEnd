package ktor.expos.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktor.expos.data.models.account_models.AccountDataSource
import ktor.expos.data.models.user_models.UserDataSource
import ktor.expos.routes.*
import ktor.expos.security.hashing.HashingService
import ktor.expos.security.token.TokenConfig
import ktor.expos.security.token.TokenService

fun Application.configureRouting(
    userDataSource: UserDataSource,
    accountDataSource: AccountDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(hashingService, userDataSource, tokenService, tokenConfig)
        signUp(hashingService, userDataSource)
        createAccount(accountDataSource)
        authenticate()
        getSecretInfo()
    }
}