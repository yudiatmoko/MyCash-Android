package com.iyam.mycash.data.network.api.model.session

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SessionRequest(
    @SerializedName("date")
    val date: String,
    @SerializedName("outletId")
    val outletId: String,
    @SerializedName("shift")
    val shift: String,
    @SerializedName("startingCash")
    val startingCash: Double,
    @SerializedName("userId")
    val userId: String
)
