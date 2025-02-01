package com.iyam.mycash.data.network.api.model.user.otp

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GenerateOtpRequest(
    @SerializedName("email")
    val email: String
)
