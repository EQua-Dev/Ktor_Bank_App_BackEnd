package ktor.expos.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ktor.expos.modules.account.daos.AccountDataSource
import ktor.expos.modules.user.daos.UserDataSource
import ktor.expos.modules.account.routes.createAccount
import ktor.expos.modules.account.routes.getMyAccounts
import ktor.expos.modules.user.routes.authenticate
import ktor.expos.modules.user.routes.getSecretInfo
import ktor.expos.modules.user.routes.signIn
import ktor.expos.modules.user.routes.signUp
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
        getMyAccounts(accountDataSource)
        authenticate()
        getSecretInfo()
    }
}