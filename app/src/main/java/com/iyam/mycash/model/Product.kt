package com.iyam.mycash.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Product(
    val id: String?,
    val name: String?,
    val description: String?,
    val price: Float?,
    val status: Boolean?,
    val stock: Int? = null,
    val categoryId: String?,
    val outletId: String?,
    val image: String?
) : Parcelable
