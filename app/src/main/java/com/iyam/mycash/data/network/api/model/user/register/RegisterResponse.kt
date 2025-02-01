package com.iyam.mycash.data.network.api.model.user.register

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.data.network.api.model.user.DataAuthResponse

@Keep
data class RegisterResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: DataAuthResponse
)
