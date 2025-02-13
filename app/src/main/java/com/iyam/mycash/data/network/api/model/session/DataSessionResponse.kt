package com.iyam.mycash.data.network.api.model.session

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.model.Session

@Keep
data class DataSessionResponse(
    @SerializedName("checkInTime")
    val checkInTime: String?,
    @SerializedName("checkOutTime")
    val checkOutTime: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("outletId")
    val outletId: String?,
    @SerializedName("shift")
    val shift: String?,
    @SerializedName("startingCash")
    val startingCash: Double?,
    @SerializedName("totalRevenue")
    val totalRevenue: Double?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("userId")
    val userId: String?
)

fun DataSessionResponse.toSession() = Session(
    id = id.orEmpty(),
    date = date.orEmpty(),
    shift = shift.orEmpty(),
    startingCash = startingCash,
    totalRevenue = totalRevenue,
    checkInTime = checkInTime.orEmpty(),
    checkOutTime = checkOutTime.orEmpty(),
    userId = userId.orEmpty(),
    outletId = outletId.orEmpty(),
    createdAt = createdAt.orEmpty(),
    updatedAt = updatedAt.orEmpty()
)

fun List<DataSessionResponse>.toSessionList() = map { it.toSession() }
