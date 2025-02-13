package com.iyam.mycash.data.network.api.model.recap

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RecapResponse(
    @SerializedName("data")
    val data: DataRecapResponse,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
