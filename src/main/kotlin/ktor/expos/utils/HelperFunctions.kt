package ktor.expos.utils
import kotlin.random.Random


object HelperFunctions {

    fun generateAccountNumber(): String {
        var seed: Long = System.currentTimeMillis()
        seed++
        val randomNumber = Random(seed).nextInt(1000000000, 9999999999.toInt())
        return randomNumber.toString()

    }
}