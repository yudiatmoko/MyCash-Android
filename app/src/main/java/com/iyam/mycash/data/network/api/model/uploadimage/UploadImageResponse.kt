package com.iyam.mycash.data.network.api.model.uploadimage

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UploadImageResponse(
    @SerializedName("data")
    val data: DataUploadImage,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
