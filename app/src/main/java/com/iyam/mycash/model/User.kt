package com.iyam.mycash.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Keep
@Serializable
data class User(
    val id: String?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val image: String?,
    val isVerified: Boolean?,
    val createdAt: String?,
    val updatedAt: String?
) : Parcelable
