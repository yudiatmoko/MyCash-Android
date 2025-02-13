package com.iyam.mycash.data.network.api.model.session

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SessionResponse(
    @SerializedName("data")
    val data: DataSessionResponse,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
