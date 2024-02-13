package ktor.expos.data.models

@kotlinx.serialization.Serializable

data class BankAppResponseData<T>(
    val status: Boolean,
    val statusCode: Int,
    val message: String,
    val data: T? = null
)
