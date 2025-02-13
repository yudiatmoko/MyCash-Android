package com.iyam.mycash.data.network.api.model.recap

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TopProductDataRecap(
    @SerializedName("name")
    val name: String?,
    @SerializedName("quantity")
    val quantity: Int?
)
