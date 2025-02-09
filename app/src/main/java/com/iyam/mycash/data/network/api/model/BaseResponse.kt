package com.iyam.mycash.data.network.api.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BaseResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)
