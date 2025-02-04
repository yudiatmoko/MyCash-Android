package com.iyam.mycash.data.network.api.model.outlet

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class OutletsResponse(
    @SerializedName("data")
    val data: List<DataOutletResponse>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
