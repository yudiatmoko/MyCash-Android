package com.iyam.mycash.data.network.api.model.user

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.model.User

@Keep
data class DataUserResponse(
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("isVerified")
    val isVerified: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
)

fun DataUserResponse.toUser() = User(
    id = this.id.orEmpty(),
    name = this.name.orEmpty(),
    email = this.email.orEmpty(),
    phoneNumber = this.phoneNumber.orEmpty(),
    isVerified = this.isVerified ?: false,
    image = this.image.orEmpty(),
    createdAt = this.createdAt.orEmpty(),
    updatedAt = this.updatedAt.orEmpty()
)
