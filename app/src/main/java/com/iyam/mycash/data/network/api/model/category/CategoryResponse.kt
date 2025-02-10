package com.iyam.mycash.data.network.api.model.category

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CategoryResponse(
    @SerializedName("data")
    val data: DataCategoryResponse,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
