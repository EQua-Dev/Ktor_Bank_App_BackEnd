package ktor.expos.utils
import kotlin.random.Random


object HelperFunctions {

    fun generateAccountNumber(): String {
        var seed: Long = System.currentTimeMillis()
        seed++
        val randomNumber = Random(seed).nextInt(1000000000, 9999999999.toInt())
        return randomNumber.toString()

    }

    fun deductBankCommissionFromTransaction(amountDeposited: Double): Double{
        val onePercentOfAmount = amountDeposited.times(0.01)
        return amountDeposited.minus(onePercentOfAmount)
    }

    fun onePercentOfAmount(amount: Double): Double{
        return amount.times(0.01)
    }
}