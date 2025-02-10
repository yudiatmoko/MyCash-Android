package com.iyam.mycash.data.network.api.model.category

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CategoriesResponse(
    @SerializedName("data")
    val data: List<DataCategoryResponse>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
