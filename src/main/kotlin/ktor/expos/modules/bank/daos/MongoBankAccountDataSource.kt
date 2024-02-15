package ktor.expos.modules.bank.daos

import ktor.expos.modules.user.models.responses.UserData
import ktor.expos.modules.account.models.responses.AccountData
import ktor.expos.modules.account.models.responses.AccountInfoResponse
import ktor.expos.modules.bank.models.requests.BankCommission
import ktor.expos.modules.bank.models.responses.BankCommissionResponse
import ktor.expos.modules.user.models.responses.UserInfo
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoBankAccountDataSource(db: CoroutineDatabase): BankAccountDataSource {
    private val bankCommissions = db.getCollection<BankCommission>()
    private val users = db.getCollection<UserData>()
    override suspend fun createCommission(bankCommission: BankCommission): Boolean {

        return bankCommissions.insertOne(bankCommission).wasAcknowledged()

    }

    override suspend fun getAllBankCommission(): BankCommissionResponse {
        val commissions = bankCommissions.find().toList()
        var totalCommissionAmount = 0.0
        commissions.forEach{ commission ->
            totalCommissionAmount = totalCommissionAmount.plus(commission.amount)
        }
        val response = BankCommissionResponse(
            totalAmount = totalCommissionAmount,
            commissions = commissions
        )

        return response
    }

}