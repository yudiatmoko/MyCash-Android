package com.iyam.mycash.data.network.api.model.transaction

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class SessionTransaction(
    @SerializedName("date")
    val date: String,
    @SerializedName("outlet")
    val outlet: OutletTransaction,
    @SerializedName("shift")
    val shift: String,
    @SerializedName("user")
    val user: UserTransaction
) : Parcelable
