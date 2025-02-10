package com.iyam.mycash.data.network.api.model.product

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProductResponse(
    @SerializedName("data")
    val data: DataProductResponse,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
