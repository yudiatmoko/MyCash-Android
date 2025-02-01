package com.iyam.mycash.data.network.api.model.user

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.model.Auth

@Keep
data class DataAuthResponse(
    @SerializedName("email")
    val email: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("isVerified")
    val isVerified: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("token")
    val token: String?
)

fun DataAuthResponse.toAuth() = Auth(
    id = this.id.orEmpty(),
    name = this.name.orEmpty(),
    email = this.email.orEmpty(),
    isVerified = this.isVerified ?: false,
    token = this.token.orEmpty()
)
