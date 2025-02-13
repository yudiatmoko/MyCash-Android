package com.iyam.mycash.data.network.api.model.recap

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class SessionDataRecap(
    @SerializedName("checkInTime")
    val checkInTime: String?,
    @SerializedName("checkOutTime")
    val checkOutTime: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("outletId")
    val outletId: String?,
    @SerializedName("paymentSummary")
    val paymentSummary: SessionPaymentSummary?,
    @SerializedName("sessionId")
    val sessionId: String?,
    @SerializedName("shift")
    val shift: String?,
    @SerializedName("startingCash")
    val startingCash: Double?,
    @SerializedName("successfulTransactions")
    val successfulTransactions: Int?,
    @SerializedName("totalRevenue")
    val totalRevenue: Double?,
    @SerializedName("totalTransactions")
    val totalTransactions: Int?,
    @SerializedName("user")
    val user: SessionUser?,
    @SerializedName("voidedTransactions")
    val voidedTransactions: Int?
) : Parcelable
