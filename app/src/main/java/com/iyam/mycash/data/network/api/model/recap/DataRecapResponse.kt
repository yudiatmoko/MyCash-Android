package com.iyam.mycash.data.network.api.model.recap

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DataRecapResponse(
    @SerializedName("details")
    val details: DetailsDataRecap?,
    @SerializedName("session")
    val sessions: List<SessionDataRecap?>,
    @SerializedName("topProducts")
    val topProducts: List<TopProductDataRecap?>
)
