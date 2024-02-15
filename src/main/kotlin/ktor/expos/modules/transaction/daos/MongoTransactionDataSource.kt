package ktor.expos.modules.transaction.daos

import ktor.expos.modules.user.models.responses.UserData
import ktor.expos.modules.bank.models.requests.BankCommission
import ktor.expos.modules.bank.models.responses.BankCommissionResponse
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoTransactionDataSource(db: CoroutineDatabase): TransactionDataSource {
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