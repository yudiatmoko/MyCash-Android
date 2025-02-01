package com.iyam.mycash.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Keep
@Serializable
data class Auth(
    val email: String?,
    val id: String?,
    val isVerified: Boolean?,
    val name: String?,
    val token: String?
) : Parcelable
