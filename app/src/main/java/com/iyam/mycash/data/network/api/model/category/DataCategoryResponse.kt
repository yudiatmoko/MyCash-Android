package com.iyam.mycash.data.network.api.model.category

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.model.Category

@Keep
data class DataCategoryResponse(
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("outletId")
    val outletId: String?,
    @SerializedName("slug")
    val slug: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
)

fun DataCategoryResponse.toCategory() = Category(
    id = this.id.orEmpty(),
    name = this.name.orEmpty(),
    slug = this.slug.orEmpty(),
    outletId = this.outletId.orEmpty(),
    createdAt = this.createdAt.orEmpty(),
    updatedAt = this.updatedAt.orEmpty()
)

fun List<DataCategoryResponse>.toCategoryList(): List<Category> {
    return this.map { it.toCategory() }
}
