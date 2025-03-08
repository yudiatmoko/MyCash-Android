package com.iyam.mycash.data.network.api.model.transaction

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TransactionsResponse(
    @SerializedName("data")
    val data: List<DataTransactionResponse>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
