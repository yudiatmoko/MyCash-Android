package com.iyam.mycash.data.network.api.model.transaction.create

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.model.Cart

@Keep
data class DetailTransactionRequest(
    @SerializedName("productId")
    val productId: String,
    @SerializedName("productQty")
    val productQty: Int
)

fun Cart.toDetailTransactionRequest(cart: Cart) = DetailTransactionRequest(
    productId = cart.productId.orEmpty(),
    productQty = cart.productQuantity
)

fun List<Cart>.toDetailTransactionRequestList() = map { it.toDetailTransactionRequest(it) }
