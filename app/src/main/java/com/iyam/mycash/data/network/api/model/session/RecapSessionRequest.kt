package com.iyam.mycash.data.network.api.model.session

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RecapSessionRequest(
    @SerializedName("checkOutTime")
    val checkOutTime: String
)
