package com.iyam.mycash.data.network.api.model.product

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProductsResponse(
    @SerializedName("data")
    val data: List<DataProductResponse>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
