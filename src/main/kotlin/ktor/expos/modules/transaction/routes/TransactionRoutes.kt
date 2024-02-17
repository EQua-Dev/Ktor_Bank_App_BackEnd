/*
 * Copyright (c) 2024.
 * All rights reserved. Richard Uzor
 */

package ktor.expos.modules.transaction.routes

import io.ktor.server.routing.*
import ktor.expos.modules.account.daos.AccountDataSource
import ktor.expos.modules.bank.daos.BankAccountDataSource
import ktor.expos.modules.transaction.daos.TransactionDataSource
import ktor.expos.modules.transaction.models.requests.Transaction
import ktor.expos.modules.transaction.models.responses.TransactionResponse

interface TransactionRoutes {
    fun Route.transferFunds(accountDataSource: AccountDataSource,
                            bankAccountDataSource: BankAccountDataSource,
                            transactionDataSource: TransactionDataSource
    )
    fun Route.getTransferHistory(accountDataSource: AccountDataSource,
                            transactionDataSource: TransactionDataSource
    )

}