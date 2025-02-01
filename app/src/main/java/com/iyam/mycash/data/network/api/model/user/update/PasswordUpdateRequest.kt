package com.iyam.mycash.data.network.api.model.user.update

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PasswordUpdateRequest(
    @SerializedName("newPassword")
    val newPassword: String,
    @SerializedName("oldPassword")
    val oldPassword: String
)
