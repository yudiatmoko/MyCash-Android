package com.iyam.mycash.data.network.api.model.user.resetpassword

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResetPasswordResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
