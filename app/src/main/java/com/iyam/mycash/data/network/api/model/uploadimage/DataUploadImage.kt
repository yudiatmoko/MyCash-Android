package com.iyam.mycash.data.network.api.model.uploadimage

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DataUploadImage(
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("sessionId")
    val sessionId: String
)
