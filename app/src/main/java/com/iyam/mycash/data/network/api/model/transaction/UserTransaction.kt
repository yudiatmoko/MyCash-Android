package com.iyam.mycash.data.network.api.model.transaction

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class UserTransaction(
    @SerializedName("name")
    val name: String
) : Parcelable
