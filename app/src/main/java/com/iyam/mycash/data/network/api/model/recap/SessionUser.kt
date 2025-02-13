package com.iyam.mycash.data.network.api.model.recap

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class SessionUser(
    @SerializedName("name")
    val name: String?
) : Parcelable
