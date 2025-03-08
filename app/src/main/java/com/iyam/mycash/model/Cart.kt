package com.iyam.mycash.model

data class Cart(
    var id: Int? = null,
    var productId: String? = null,
    val productName: String,
    val productPrice: Double,
    val productImg: String,
    val productStock: Int? = null,
    var productQuantity: Int = 0
)
