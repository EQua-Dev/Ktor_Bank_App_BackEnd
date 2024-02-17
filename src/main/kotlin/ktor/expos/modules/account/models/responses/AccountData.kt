package ktor.expos.modules.account.models.responses

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import ktor.expos.modules.user.models.responses.UserData
import ktor.expos.modules.user.models.responses.UserInfo
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

@Serializable
data class AccountData(
    @BsonId var accountId: String = ObjectId().toHexString(),
    val accountType: String,
    @field:BsonProperty(useDiscriminator = true) val accountOwnerId: UserInfo,
    var accountNumber: String,
    val accountBalance: Double,
    val dateCreated: String
)

