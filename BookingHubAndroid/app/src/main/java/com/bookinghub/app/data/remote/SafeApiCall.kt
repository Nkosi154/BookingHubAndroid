package com.bookinghub.app.data.remote

import retrofit2.Response

/**
 * Wraps a Retrofit call: success -> Result.success(body), failure -> Result.failure with
 * the backend's error message when it sent one (DRF returns {"detail": "..."} or
 * per-field validation errors), otherwise a sensible fallback.
 */
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) Result.success(body)
            else Result.failure(Exception("Empty response from server."))
        } else {
            val errorText = response.errorBody()?.string()
            Result.failure(Exception(parseErrorMessage(errorText) ?: "Request failed (${response.code()})."))
        }
    } catch (e: java.net.ConnectException) {
        Result.failure(Exception("Can't reach the server. Check BASE_URL and that the Django server is running."))
    } catch (e: java.net.SocketTimeoutException) {
        Result.failure(Exception("The server took too long to respond."))
    } catch (e: Exception) {
        Result.failure(Exception(e.message ?: "Something went wrong."))
    }
}

private fun parseErrorMessage(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    return try {
        val obj = com.google.gson.JsonParser.parseString(raw).asJsonObject
        when {
            obj.has("detail") -> obj.get("detail").asString
            else -> obj.entrySet().joinToString("\n") { (key, value) ->
                val text = if (value.isJsonArray) value.asJsonArray.joinToString(" ") { it.asString } else value.asString
                "$key: $text"
            }
        }
    } catch (e: Exception) {
        raw
    }
}
