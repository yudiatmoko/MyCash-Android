package com.iyam.mycash.data.network.api.model.transaction

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.model.TransactionDetail

@Keep
data class DetailTransaction(
    @SerializedName("id")
    val id: String,
    @SerializedName("productId")
    val productId: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("productPrice")
    val productPrice: Double,
    @SerializedName("productQty")
    val productQty: Int,
    @SerializedName("totalPrice")
    val totalPrice: Double,
    @SerializedName("transactionId")
    val transactionId: String
)

fun DetailTransaction.toDetailTransaction() = TransactionDetail(
    id = id,
    productId = productId,
    productName = productName,
    productPrice = productPrice,
    productQty = productQty,
    totalPrice = totalPrice,
    transactionId = transactionId
)

fun List<DetailTransaction>.toDetailTransactionList() = map { it.toDetailTransaction() }
