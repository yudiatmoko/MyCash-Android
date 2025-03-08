package com.iyam.mycash.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carts")
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "product_id")
    var productId: String? = null,
    @ColumnInfo(name = "product_name")
    val productName: String,
    @ColumnInfo(name = "product_price")
    val productPrice: Double,
    @ColumnInfo(name = "product_img")
    val productImg: String,
    @ColumnInfo(name = "product_stock")
    var productStock: Int? = null,
    @ColumnInfo(name = "product_quantity")
    var productQuantity: Int = 0
)
