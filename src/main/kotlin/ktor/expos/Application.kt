package ktor.expos

import io.ktor.server.application.*
import ktor.expos.plugins.*
import ktor.expos.data.db.BankAppDatabase
import ktor.expos.modules.account.daos.MongoAccountDataSource
import ktor.expos.modules.bank.daos.MongoBankAccountDataSource
import ktor.expos.modules.transaction.daos.MongoTransactionDataSource
import ktor.expos.modules.user.daos.MongoUserDataSource
import ktor.expos.security.hashing.SHA256HashingService
import ktor.expos.security.token.JwtTokenService
import ktor.expos.security.token.TokenConfig

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val userDataSource = MongoUserDataSource(BankAppDatabase.database)
    val accountDataSource = MongoAccountDataSource(BankAppDatabase.database)
    val bankAccountDataSource = MongoBankAccountDataSource(BankAppDatabase.database)
    val transactionDataSource = MongoTransactionDataSource(BankAppDatabase.database)

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L *24L, //milliseconds equivalent of one year
        secret = System.getenv("JWT_SECRET")
    )

    val hashingService = SHA256HashingService()


    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, accountDataSource, bankAccountDataSource, transactionDataSource, hashingService, tokenService, tokenConfig)
}
