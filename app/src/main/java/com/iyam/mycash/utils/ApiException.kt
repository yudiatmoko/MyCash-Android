package com.iyam.mycash.utils

import com.google.gson.Gson
import com.iyam.mycash.data.network.api.model.BaseResponse
import retrofit2.Response

class ApiException(
    override val message: String?,
    val httpCode: Int,
    val errorResponse: Response<*>?
) : Exception() {

    fun getParsedError(): BaseResponse? {
        val body = errorResponse?.errorBody()?.string().orEmpty()
        return if (body.isNotEmpty()) {
            try {
                Gson().fromJson(body, BaseResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}
