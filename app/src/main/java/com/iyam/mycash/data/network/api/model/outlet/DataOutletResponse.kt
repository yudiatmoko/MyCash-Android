package com.iyam.mycash.data.network.api.model.outlet

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iyam.mycash.model.Outlet

@Keep
data class DataOutletResponse(
    @SerializedName("address")
    val address: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("district")
    val district: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("province")
    val province: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("userId")
    val userId: String?
)

fun DataOutletResponse.toOutlet() = Outlet(
    id = this.id.orEmpty(),
    name = this.name.orEmpty(),
    type = this.type.orEmpty(),
    phoneNumber = this.phoneNumber.orEmpty(),
    address = this.address.orEmpty(),
    district = this.district.orEmpty(),
    city = this.city.orEmpty(),
    province = this.province.orEmpty(),
    image = this.image.orEmpty(),
    userId = this.userId.orEmpty(),
    createdAt = this.createdAt.orEmpty(),
    updatedAt = this.updatedAt.orEmpty()
)

fun List<DataOutletResponse>.toOutletList(): List<Outlet> {
    return this.map { it.toOutlet() }
}
