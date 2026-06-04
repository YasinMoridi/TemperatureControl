package com.yasinmoridi.temperaturecontrol.data.remote.model

object NetworkMessages {

    const val SUCCESS = "اطلاعات با موفقیت دریافت شد"
    const val EMPTY_BODY = "اطلاعاتی از سرور دریافت نشد"
    const val UNKNOWN_ERROR = "خطای غیرمنتظره‌ای رخ داد"
    const val NETWORK_ERROR = "اتصال به اینترنت برقرار نیست"
    const val REQUEST_FAILED = "درخواست ناموفق بود"
    const val SERVER_ERROR = "خطا در ارتباط با سرور"
    const val TIMEOUT_ERROR = "زمان پاسخ سرور به پایان رسید"
    const val UNAUTHORIZED = "دسترسی غیرمجاز"
    const val FORBIDDEN = "شما اجازه دسترسی ندارید"
    const val NOT_FOUND = "اطلاعات مورد نظر یافت نشد"

    fun httpError(code: Int): String {
        return when (code) {
            401 -> UNAUTHORIZED
            403 -> FORBIDDEN
            404 -> NOT_FOUND
            408 -> TIMEOUT_ERROR
            in 500..599 -> SERVER_ERROR
            else -> UNKNOWN_ERROR
        }
    }
}