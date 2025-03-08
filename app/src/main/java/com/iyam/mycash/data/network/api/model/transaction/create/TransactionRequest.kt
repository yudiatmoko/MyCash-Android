package com.iyam.mycash.data.network.api.model.transaction.create

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TransactionRequest(
    @SerializedName("detail")
    val detail: List<DetailTransactionRequest>,
    @SerializedName("note")
    val note: String,
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("totalPayment")
    val totalPayment: Double
)
