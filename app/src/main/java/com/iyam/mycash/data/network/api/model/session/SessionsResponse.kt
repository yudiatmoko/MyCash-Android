package com.iyam.mycash.data.network.api.model.session

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SessionsResponse(
    @SerializedName("data")
    val data: List<DataSessionResponse>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
