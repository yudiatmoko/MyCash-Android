package com.iyam.mycash.data.network.api.model.user.otp

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class VerifyOtpResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)
