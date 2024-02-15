package ktor.expos.modules.account.models.responses

import kotlinx.serialization.Serializable
import ktor.expos.modules.user.models.responses.UserData
import ktor.expos.modules.user.models.responses.UserInfo

@Serializable
data class AccountDummyResponse(
    val userId: String,
    val accountNumber: String,
    val accountBalance: Double
)

@Serializable
data class AccountInfoResponse(
    val account: AccountData,
    val accountOwner: UserInfo,
)
