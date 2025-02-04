package com.iyam.mycash.data.network.api.model.outlet

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class OutletRequest(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("district")
    val district: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("province")
    val province: String,
    @SerializedName("type")
    val type: String
)
