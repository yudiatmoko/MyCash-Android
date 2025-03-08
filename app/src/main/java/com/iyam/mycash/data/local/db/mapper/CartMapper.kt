package com.iyam.mycash.data.local.db.mapper

import com.iyam.mycash.data.local.db.entity.CartEntity
import com.iyam.mycash.model.Cart

fun CartEntity?.toCart() = Cart(
    id = this?.id ?: 0,
    productId = this?.productId.orEmpty(),
    productQuantity = this?.productQuantity ?: 0,
    productStock = this?.productStock,
    productPrice = this?.productPrice ?: 0.0,
    productName = this?.productName.orEmpty(),
    productImg = this?.productImg.orEmpty()
)

fun Cart?.toCartEntity() = CartEntity(
    id = this?.id,
    productId = this?.productId.orEmpty(),
    productQuantity = this?.productQuantity ?: 0,
    productStock = this?.productStock,
    productPrice = this?.productPrice ?: 0.0,
    productName = this?.productName.orEmpty(),
    productImg = this?.productImg.orEmpty()
)

fun List<CartEntity?>.toCartList() = this.map { it.toCart() }
