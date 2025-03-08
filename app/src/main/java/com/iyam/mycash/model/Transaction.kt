package com.iyam.mycash.model

import android.os.Parcelable
import com.iyam.mycash.data.network.api.model.transaction.SessionTransaction
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val id: String?,
    val number: Int?,
    val date: String?,
    val totalPrice: Double?,
    val totalPayment: Double?,
    val totalCharge: Double?,
    val paymentMethod: String?,
    val note: String?,
    val sessionId: String?,
    val isVoided: Boolean?,
    val createdAt: String?,
    val updatedAt: String?,
    val details: List<TransactionDetail>?,
    val session: SessionTransaction?
) : Parcelable
