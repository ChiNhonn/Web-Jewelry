package com.aura.jewelry.core.common

import com.aura.jewelry.core.network.model.ApiResponseDto
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Standard Network Resource wrapper representing UI and repository state.
 */
sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T, val message: String? = null) : NetworkResult<T>
    data class Error(val message: String, val code: Int? = null, val throwable: Throwable? = null) : NetworkResult<Nothing>
    data object Loading : NetworkResult<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data
}

/**
 * Safe API call helper that catches network exceptions and parses error responses cleanly.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> ApiResponseDto<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.success && response.data != null) {
            NetworkResult.Success(response.data, response.message)
        } else if (response.success) {
            @Suppress("UNCHECKED_CAST")
            NetworkResult.Success(null as T, response.message)
        } else {
            NetworkResult.Error(response.message ?: "Đã có lỗi xảy ra từ máy chủ")
        }
    } catch (e: HttpException) {
        val errorMsg = when (e.code()) {
            400 -> "Dữ liệu yêu cầu không hợp lệ (400)"
            401 -> "Phiên đăng nhập đã hết hạn (401)"
            403 -> "Bạn không có quyền truy cập chức năng này (403)"
            404 -> "Không tìm thấy dữ liệu yêu cầu (404)"
            409 -> "Dữ liệu xung đột hoặc đã tồn tại (409)"
            500 -> "Lỗi hệ thống máy chủ nội bộ (500)"
            else -> "Lỗi kết nối máy chủ (${e.code()})"
        }
        NetworkResult.Error(message = errorMsg, code = e.code(), throwable = e)
    } catch (e: ConnectException) {
        NetworkResult.Error(message = "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng hoặc backend.", throwable = e)
    } catch (e: SocketTimeoutException) {
        NetworkResult.Error(message = "Kết nối quá hạn (Timeout). Vui lòng thử lại.", throwable = e)
    } catch (e: IOException) {
        NetworkResult.Error(message = "Lỗi đường truyền mạng.", throwable = e)
    } catch (e: Exception) {
        NetworkResult.Error(message = e.localizedMessage ?: "Lỗi không xác định", throwable = e)
    }
}
