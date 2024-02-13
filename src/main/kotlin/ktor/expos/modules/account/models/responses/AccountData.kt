package ktor.expos.modules.account.models.responses

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class AccountData(
    @BsonId var accountId: String = ObjectId().toHexString(),
    val accountType: String,
    val accountOwnerId: String,
    var accountNumber: String,
    val accountBalance: String,
    val dateCreated: String
)

