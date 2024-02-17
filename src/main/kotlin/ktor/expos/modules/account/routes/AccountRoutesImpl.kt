package ktor.expos.modules.account.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktor.expos.data.models.BankAppResponseData
import ktor.expos.modules.account.models.responses.AccountData
import ktor.expos.modules.account.daos.AccountDataSource
import ktor.expos.modules.account.models.requests.CreateAccountRequest
import ktor.expos.modules.account.models.responses.AccountDummyResponse
import ktor.expos.modules.bank.daos.BankAccountDataSource
import ktor.expos.modules.bank.models.requests.BankCommission
import ktor.expos.modules.transaction.daos.TransactionDataSource
import ktor.expos.modules.transaction.models.requests.Transaction
import ktor.expos.modules.user.daos.UserDataSource
import ktor.expos.modules.user.models.responses.UserInfo
import ktor.expos.services.ServiceHelpers.getUserIdFromToken
import ktor.expos.utils.HelperFunctions
import ktor.expos.utils.HelperFunctions.deductBankCommissionFromTransaction

/*
* The route extension file to sign up users
* pass as arguments the interfaces of the related services as opposed to the implementation classes...
* ...that way, if there is a change in the database used, the required services (interface) will remain unchanged
* */
 object AccountRoutesImpl: AccountRoutes{
    override fun Route.createAccount(
        accountDataSource: AccountDataSource,
        bankAccountDataSource: BankAccountDataSource,
        transactionDataSource: TransactionDataSource,
        userDataSource: UserDataSource
    ) {
        authenticate {
            post("create-account") {
                // Check if the request contains an authorization header with a bearer token

                val userId = getUserIdFromToken(call)
                val userAccount = userDataSource.getUserByUserId(userId)
                val userExists = userAccount != null



                val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                    call.respond(HttpStatusCode.BadRequest, BankAppResponseData(
                        false,
                        HttpStatusCode.BadRequest.value,
                        HttpStatusCode.BadRequest.description,
                        "Field cannot be empty"
                    ))
                    return@post
                }


                if (!userExists) {
                    call.respond(HttpStatusCode.NotFound, BankAppResponseData(
                        false,
                        HttpStatusCode.NotFound.value,
                        HttpStatusCode.NotFound.description,
                        "User not found"
                    ))
                    return@post
                }

                val userAccountInfo = UserInfo(
                    userId = userId,
                    userName = userAccount?.userName!!
                )


                val areFieldsBlank = request.accountType.isBlank()
                //you could also run other validation checks here
                val minimumDepositAmountNotEnough = request.initialDeposit < 100.0

                if (areFieldsBlank) {
                    call.respond(HttpStatusCode.Conflict, BankAppResponseData(
                        false,
                        HttpStatusCode.Conflict.value,
                        HttpStatusCode.Conflict.description,
                        "Fields are required"
                    ))
                    return@post
                }

                if (minimumDepositAmountNotEnough){
                    call.respond(HttpStatusCode.Conflict, BankAppResponseData(
                        false,
                        HttpStatusCode.Conflict.value,
                        HttpStatusCode.Conflict.description,
                        "Minimum of ₦100 must be deposited at account creation"
                    ))
                    return@post
                }

                //bank takes 1% of every transaction deposit



                //generate the account number here
                val accountNumber = HelperFunctions.generateAccountNumber()
                val account = AccountData(
                    accountType = request.accountType,
                    accountOwnerId = userAccountInfo,
                    dateCreated = System.currentTimeMillis().toString(),
                    accountBalance = deductBankCommissionFromTransaction(request.initialDeposit),
                    accountNumber = accountNumber
                )
                val transaction = Transaction(
                    transactionAmount = account.accountBalance,
                    transactionFrom = account,
                    transactionNarration = "Account Opening",
                    transactionDate = System.currentTimeMillis().toString(),
                    transactionTo = null
                )

                val commission = BankCommission(
                    dateCreated = System.currentTimeMillis().toString(),
                    transactionID = transaction.transactionID,
                    amount = request.initialDeposit.times(0.01)
                )

                val wasAccountCreationAcknowledged = accountDataSource.createAccount(account)
                val wasTransactionAcknowledged = transactionDataSource.createTransaction(transaction)
                val wasCommissionAcknowledged = bankAccountDataSource.createCommission(commission)


                //then write to the accounts table, the transactions table and the bank commission table


                if (!wasAccountCreationAcknowledged || !wasTransactionAcknowledged || !wasCommissionAcknowledged) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        BankAppResponseData(
                            false,
                            HttpStatusCode.Conflict.value,
                            HttpStatusCode.Conflict.description,
                            "Error creating account"
                        )
                    )
                    return@post
                }
                val createdAccount = AccountDummyResponse(
                    userId = userId,
                    accountNumber = account.accountNumber,
                    accountBalance = account.accountBalance
                )
                call.respond(
                    HttpStatusCode.OK,
                    BankAppResponseData(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, createdAccount)


                )
            }
        }

    }

    override fun Route.getAllMyAccounts(
        accountDataSource: AccountDataSource
    ) {
        authenticate {
            get("my-accounts") {
                // Check if the request contains an authorization header with a bearer token

                val userId = getUserIdFromToken(call)

                val myAccounts = accountDataSource.getAllMyAccounts(userId)

                myAccounts?.let {
                    call.respond(
                        HttpStatusCode.OK,
                        BankAppResponseData(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, it)

                        //SimpleResponse(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, it)
                    )
                } ?: call.respond(
                    HttpStatusCode.NotFound,
                    BankAppResponseData(false, HttpStatusCode.NotFound.value, "No account found", it)

                    //SimpleResponse(false, HttpStatusCode.NotFound.value, "There are no messages", it)

                )
            }
        }

    }

    override fun Route.getAccountInfoByAccountNumber(accountDataSource: AccountDataSource) {
        authenticate {
            get("find-account/{accountNumber}") {
                // Check if the request contains an authorization header with a bearer token

                val accountNumber = call.parameters["accountNumber"]

                val userAccount = accountDataSource.getAccountInfoByAccountNumber(accountNumber!!)

                userAccount?.let {account ->
                    call.respond(
                        HttpStatusCode.OK,
                        BankAppResponseData(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, account)
                    )
                } ?: call.respond(
                    HttpStatusCode.NotFound,
                    BankAppResponseData(false, HttpStatusCode.NotFound.value, "No account found", it)
                )
            }
        }
    }

}


/*
* Function to know if a user's token is still valid
* */
/*
fun Route.authenticate(){
    //use our default authentication function to carry out the logic
    authenticate {
        get("authenticate"){
            call.respond(HttpStatusCode.OK)
        }
    }
}
*/

/*
* Function to get the user id from the authenticated user
* */
/*
fun Route.getSecretInfo(){
    authenticate{
        get("secret"){
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}
*/

















