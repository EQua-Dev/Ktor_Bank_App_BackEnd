package ktor.expos.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktor.expos.data.models.account_models.AccountData
import ktor.expos.data.models.account_models.AccountDataSource
import ktor.expos.data.models.requests.CreateAccountRequest
import ktor.expos.data.models.requests.SignInRequest
import ktor.expos.data.models.requests.SignUpRequest
import ktor.expos.data.models.responses.AccountDummyResponse
import ktor.expos.data.models.responses.AuthResponse
import ktor.expos.data.models.user_models.UserData
import ktor.expos.data.models.user_models.UserDataSource
import ktor.expos.security.hashing.HashingService
import ktor.expos.security.hashing.SaltedHash
import ktor.expos.security.token.TokenClaim
import ktor.expos.security.token.TokenConfig
import ktor.expos.security.token.TokenService
import ktor.expos.services.ServiceHelpers.getUserIdFromToken
import ktor.expos.utils.HelperFunctions
import org.apache.commons.codec.digest.DigestUtils
import org.bson.types.ObjectId

/*
* The route extension file to sign up users
* pass as arguments the interfaces of the related services as opposed to the implementation classes...
* ...that way, if there is a change in the database used, the required services (interface) will remain unchanged
* */

fun Route.createAccount(
    accountDataSource: AccountDataSource
) {
    authenticate {
        post("create-account") {
            // Check if the request contains an authorization header with a bearer token
            /*val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
            application.log.info("token $token")
            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            // Validate the token
            val principal = call.authentication.principal<UserIdPrincipal>()
            application.log.info("principal $principal")

            //val userId = principal?.payload?.getClaim("userId")?.asString()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }*/
            val userId = getUserIdFromToken(call)

            val request = call.receiveOrNull<CreateAccountRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val areFieldsBlank = request.accountType.isBlank()
            //you could also run other validation checks here

            if (areFieldsBlank) {
                call.respond(HttpStatusCode.Conflict)
                return@post
            }
             //generate the account number here
             val accountNumber = HelperFunctions.generateAccountNumber()
             val account = AccountData(
                 accountType = request.accountType,
                 accountOwnerId = userId,
                 dateCreated = System.currentTimeMillis().toString(),
                 accountBalance = "0.0",
                 accountNumber = accountNumber
             )

            application.log.info("userId = ${account.accountOwnerId}")
                    val wasAcknowledged = accountDataSource.insertAccount(account)
                    if (!wasAcknowledged){
                        call.respond(HttpStatusCode.Conflict)
                        return@post
                    }

            call.respond(
                status = HttpStatusCode.OK,
                message = AccountDummyResponse(
                    userId = userId,
                    accountNumber = account.accountNumber,
                    accountBalance = account.accountBalance
                )
            )
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

















