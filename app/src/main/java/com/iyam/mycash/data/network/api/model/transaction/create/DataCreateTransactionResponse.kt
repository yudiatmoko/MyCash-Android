package com.iyam.mycash.data.network.api.model.transaction.create

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DataCreateTransactionResponse(
    @SerializedName("transactionId")
    val transactionId: String
)
