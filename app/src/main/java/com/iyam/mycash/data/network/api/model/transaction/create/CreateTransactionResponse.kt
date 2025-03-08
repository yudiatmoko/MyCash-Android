package com.iyam.mycash.data.network.api.model.transaction.create

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CreateTransactionResponse(
    @SerializedName("data")
    val `data`: DataCreateTransactionResponse,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
