package com.iyam.mycash.data.network.api.model.user.updateimport androidx.annotation.Keepimport com.google.gson.annotations.SerializedNameimport com.iyam.mycash.data.network.api.model.BaseResponseimport com.iyam.mycash.data.network.api.model.user.DataUserResponse@Keepdata class PasswordUpdateResponse(    @SerializedName("data")    val data: DataUserResponse?,    val baseResponse: BaseResponse)