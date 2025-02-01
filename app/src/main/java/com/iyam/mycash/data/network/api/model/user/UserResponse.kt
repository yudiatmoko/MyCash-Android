package com.iyam.mycash.data.network.api.model.user

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserResponse(
    @SerializedName("data")
    val data: DataUserResponse,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)
