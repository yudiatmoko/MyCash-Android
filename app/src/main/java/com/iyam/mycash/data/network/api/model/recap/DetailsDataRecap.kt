package com.iyam.mycash.data.network.api.model.recap

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DetailsDataRecap(
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("paymentSummary")
    val paymentSummary: SessionPaymentSummary,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("successfulTransactions")
    val successfulTransactions: Int,
    @SerializedName("totalRevenue")
    val totalRevenue: Int,
    @SerializedName("totalSessions")
    val totalSessions: Int,
    @SerializedName("totalTransactions")
    val totalTransactions: Int,
    @SerializedName("voidedTransactions")
    val voidedTransactions: Int
)
