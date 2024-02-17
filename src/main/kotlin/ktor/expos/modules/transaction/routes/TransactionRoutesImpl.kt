/*
 * Copyright (c) 2024.
 * All rights reserved. Richard Uzor
 */

package ktor.expos.modules.transaction.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import ktor.expos.data.models.BankAppResponseData
import ktor.expos.modules.account.daos.AccountDataSource
import ktor.expos.modules.bank.daos.BankAccountDataSource
import ktor.expos.modules.bank.models.requests.BankCommission
import ktor.expos.modules.transaction.daos.TransactionDataSource
import ktor.expos.modules.transaction.models.requests.Transaction
import ktor.expos.modules.transaction.models.requests.TransactionRequest
import ktor.expos.modules.transaction.models.responses.TransactionResponse
import ktor.expos.services.ServiceHelpers
import ktor.expos.utils.HelperFunctions.onePercentOfAmount

object TransactionRoutesImpl : TransactionRoutes {
    override fun Route.transferFunds(
        accountDataSource: AccountDataSource,
        bankAccountDataSource: BankAccountDataSource,
        transactionDataSource: TransactionDataSource
    ) {
        authenticate {
            post("transfer-funds/{ownerAccount}") {
                //check the account of the sender exists from the userId from the token and pull it
                //confirm that the sender has sufficient funds (including the 1% commission)
                //confirm that the receiver account exists and pull it
                //subtract the balance of the sender account including the 1% commission
                //add the balance to the receiver account

                val requiredBodyType = TransactionRequest::class

                val userId = ServiceHelpers.getUserIdFromToken(call)
                val senderAccountNumber = call.parameters["ownerAccount"]
                val senderAccount = accountDataSource.getAccountByAccountNumber(senderAccountNumber!!)



                val request = call.receiveOrNull<TransactionRequest>() ?: kotlin.run {
                    call.respond(
                        HttpStatusCode.BadRequest, BankAppResponseData(
                            false,
                            HttpStatusCode.BadRequest.value,
                            HttpStatusCode.BadRequest.description,
                            "Fields cannot be empty"
                        )
                    )
                    return@post
                }



                if (senderAccount == null) {
                    call.respond(
                        HttpStatusCode.Conflict, BankAppResponseData(
                            false,
                            HttpStatusCode.Conflict.value,
                            HttpStatusCode.Conflict.description,
                            "sender Account does not exist"
                        )
                    )
                    return@post
                }

                if (userId != senderAccount.accountOwnerId.userId) {
                    call.respond(
                        HttpStatusCode.Unauthorized, BankAppResponseData(
                            false,
                            HttpStatusCode.Unauthorized.value,
                            HttpStatusCode.Unauthorized.description,
                            "Requested sender account does not belong to this user"
                        )
                    )
                    return@post
                }


                val receiverAccount = accountDataSource.getAccountByAccountNumber(request.transactionTo)

                val accountToIsEmpty = request.transactionTo.isBlank()
                val amountToDebit = request.transactionAmount.plus(onePercentOfAmount(request.transactionAmount))
                val transactionAmountNotEnough = request.transactionAmount < 1
                val bankBalanceNotEnough = senderAccount.accountBalance < amountToDebit

                if (receiverAccount == null) {
                    call.respond(
                        HttpStatusCode.Conflict, BankAppResponseData(
                            false,
                            HttpStatusCode.Conflict.value,
                            HttpStatusCode.Conflict.description,
                            "receiver Account does not exist"
                        )
                    )
                    return@post
                }
                if (accountToIsEmpty) {
                    call.respond(
                        HttpStatusCode.Conflict, BankAppResponseData(
                            false,
                            HttpStatusCode.Conflict.value,
                            HttpStatusCode.Conflict.description,
                            "Receiver account is required"
                        )
                    )
                    return@post
                }

                if (transactionAmountNotEnough) {
                    call.respond(
                        HttpStatusCode.Conflict, BankAppResponseData(
                            false,
                            HttpStatusCode.Conflict.value,
                            HttpStatusCode.Conflict.description,
                            "Minimum amount of transaction is ₦1"
                        )
                    )
                    return@post
                }
                if (bankBalanceNotEnough) {
                    call.respond(
                        HttpStatusCode.Conflict, BankAppResponseData(
                            false,
                            HttpStatusCode.Conflict.value,
                            HttpStatusCode.Conflict.description,
                            "Insufficient funds"
                        )
                    )
                    return@post
                }

                //do the transfer math
                val newSenderBalance = senderAccount.accountBalance.minus(amountToDebit)
                val newReceiverBalance = receiverAccount.accountBalance.plus(request.transactionAmount)

                //write to the sender account, the receiver account, the transactions table and the bank commission table
                val transaction = Transaction(
                    transactionAmount = request.transactionAmount,
                    transactionFrom = senderAccount,
                    transactionNarration = request.transactionNarration,
                    transactionDate = System.currentTimeMillis().toString(),
                    transactionTo = receiverAccount
                )

                val commission = BankCommission(
                    dateCreated = System.currentTimeMillis().toString(),
                    transactionID = transaction.transactionID,
                    amount = transaction.transactionAmount.times(0.01)
                )

                val wasSenderBalanceUpdated =
                    accountDataSource.updateAccountBalance(senderAccountNumber, newSenderBalance)
                val wasReceiverBalanceUpdated =
                    accountDataSource.updateAccountBalance(receiverAccount.accountNumber, newReceiverBalance)
                val wasNewTransactionWritten = transactionDataSource.createTransaction(transaction)
                val wasCommissionWritten = bankAccountDataSource.createCommission(commission)

                if (!wasSenderBalanceUpdated || !wasReceiverBalanceUpdated || !wasNewTransactionWritten || !wasCommissionWritten) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        BankAppResponseData(
                            false,
                            HttpStatusCode.Conflict.value,
                            HttpStatusCode.Conflict.description,
                            "Error with transaction"
                        )
                    )
                    return@post
                }

                val transactionReceipt = TransactionResponse(
                    transactionID = transaction.transactionID,
                    transactionTime = transaction.transactionDate,
                    transactionAmount = transaction.transactionAmount,
                    transactionNarration = transaction.transactionNarration,
                    transactionReceiver = receiverAccount,
                    transactionSender = senderAccount
                )

                call.respond(
                    HttpStatusCode.OK,
                    BankAppResponseData(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, transactionReceipt)
                )
            }
        }
    }

    override fun Route.getTransferHistory(
        accountDataSource: AccountDataSource,
        transactionDataSource: TransactionDataSource
    ) {
        authenticate { 
            get("my-transfer-history"){
                val userId = ServiceHelpers.getUserIdFromToken(call)

                val myTransactions = transactionDataSource.getAllTransactionsOfUser(userId)

                application.log.info("userId: $userId")

                myTransactions?.let {transactions ->
                    call.respond(
                        HttpStatusCode.OK,
                        BankAppResponseData(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, transactions)

                    )
                } ?: call.respond(
                    HttpStatusCode.NotFound,
                    BankAppResponseData(false, HttpStatusCode.NotFound.value, "No transactions found", it)


                )
            }
        }
    }
}