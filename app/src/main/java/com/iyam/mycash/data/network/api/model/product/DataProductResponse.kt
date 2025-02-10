package com.iyam.mycash.data.network.api.model.product

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.model.Product

@Keep
data class DataProductResponse(
    @SerializedName("categoryId")
    val categoryId: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("outletId")
    val outletId: String?,
    @SerializedName("price")
    val price: Float?,
    @SerializedName("status")
    val status: Boolean?,
    @SerializedName("stock")
    val stock: Int?,
    @SerializedName("updatedAt")
    val updatedAt: String?
)

fun DataProductResponse.toProduct() = Product(
    id = this.id.orEmpty(),
    name = this.name.orEmpty(),
    description = this.description.orEmpty(),
    price = this.price,
    status = this.status ?: false,
    stock = this.stock,
    categoryId = this.categoryId.orEmpty(),
    outletId = this.outletId.orEmpty(),
    image = this.image.orEmpty()
)

fun List<DataProductResponse>.toProductList(): List<Product> {
    return this.map { it.toProduct() }
}
