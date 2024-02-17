package ktor.expos.modules.account.daos

import com.mongodb.client.model.Filters
import ktor.expos.modules.user.models.responses.UserData
import ktor.expos.modules.account.models.responses.AccountData
import ktor.expos.modules.account.models.responses.AccountInfoResponse
import ktor.expos.modules.user.models.responses.UserInfo
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class MongoAccountDataSource(db: CoroutineDatabase): AccountDataSource {
    private val accounts = db.getCollection<AccountData>()
    private val users = db.getCollection<UserData>()

    override suspend fun getAccountByAccountNumber(accountNumber: String): AccountData? {
        return accounts.findOne(AccountData::accountNumber eq accountNumber)
    }

    override suspend fun createAccount(accountData: AccountData): Boolean {
        //val userAccount = accounts.findOneById(accountData.accountId)
        //val accountExists = userAccount != null

        val userExists = users.findOneById(ObjectId(accountData.accountOwnerId.userId)) !=null



        return if (!userExists) {
            false
        }else{
            accounts.insertOne(accountData).wasAcknowledged()
        }


    }

    override suspend fun getAllMyAccounts(ownerId: String): List<AccountData> {
        val userAccounts = accounts.find(Filters.eq("accountOwnerId.userId", ownerId)).toList()
      /*  val userMappedAccounts: MutableList<AccountDataResponse>
        userAccounts.forEach { account ->
            val newAccount = account.copy(accountId = account.)
        }*/
        return userAccounts
    }

    override suspend fun getAccountInfoByAccountNumber(accountNumber: String): AccountInfoResponse {
        val searchedAccount = accounts.findOne(AccountData::accountNumber eq accountNumber)
        val userObjectID = ObjectId(searchedAccount?.accountOwnerId!!.userId)
        val searchAccountOwner = users.findOneById(userObjectID)
        val returnedAccountOwner = UserInfo(
            userId = searchAccountOwner?.userId!!.toHexString(),
            userName = searchAccountOwner.userName
        )
        val returnedAccount = AccountInfoResponse(
            account = searchedAccount,
            accountOwner = returnedAccountOwner
        )

        return returnedAccount
    }

    override suspend fun updateAccountBalance(accountNumber: String, newAmount: Double): Boolean {
        val account = accounts.findOne(AccountData::accountNumber eq accountNumber)

        return if (account == null){
            false
        }else{
            accounts.updateOne(AccountData::accountNumber eq accountNumber, setValue(AccountData::accountBalance, newAmount)).wasAcknowledged()
        }
    }
}