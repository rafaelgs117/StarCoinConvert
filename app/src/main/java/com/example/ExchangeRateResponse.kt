package com.example

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRateResponse(
    @Json(name = "result") val result: String,
    @Json(name = "base_code") val base_code: String,
    @Json(name = "conversion_rates") val conversion_rates: Map<String, Double>,
    @Json(name = "time_last_update_utc") val time_last_update_utc: String
)
