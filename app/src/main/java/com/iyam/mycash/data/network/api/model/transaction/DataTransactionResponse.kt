package com.iyam.mycash.data.network.api.model.transaction

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.model.Transaction

@Keep
data class DataTransactionResponse(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("details")
    val details: List<DetailTransaction>,
    @SerializedName("id")
    val id: String,
    @SerializedName("isVoided")
    val isVoided: Boolean,
    @SerializedName("note")
    val note: String,
    @SerializedName("number")
    val number: Int,
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    @SerializedName("session")
    val session: SessionTransaction,
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("totalCharge")
    val totalCharge: Double,
    @SerializedName("totalPayment")
    val totalPayment: Double,
    @SerializedName("totalPrice")
    val totalPrice: Double,
    @SerializedName("updatedAt")
    val updatedAt: String
)

fun DataTransactionResponse.toTransaction() = Transaction(
    id = id,
    number = number,
    date = date,
    totalPrice = totalPrice,
    totalPayment = totalPayment,
    totalCharge = totalCharge,
    paymentMethod = paymentMethod,
    note = note,
    sessionId = sessionId,
    isVoided = isVoided,
    createdAt = createdAt,
    updatedAt = updatedAt,
    details = details.toDetailTransactionList(),
    session = session
)

fun List<DataTransactionResponse>.toTransactionList() = this.map { it.toTransaction() }
