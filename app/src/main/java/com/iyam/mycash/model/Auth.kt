package com.iyam.mycash.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Keep
@Serializable
data class Auth(
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
) : Parcelable
