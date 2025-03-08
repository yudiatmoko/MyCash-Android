package com.iyam.mycash.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionDetail(
    val id: String?,
    val productId: String?,
    val productName: String?,
    val productPrice: Double?,
    val productQty: Int?,
    val totalPrice: Double?,
    val transactionId: String?
) : Parcelable
