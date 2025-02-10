package com.iyam.mycash.data.network.api.model.category

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CategoryRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("outletId")
    val outletId: String
)
