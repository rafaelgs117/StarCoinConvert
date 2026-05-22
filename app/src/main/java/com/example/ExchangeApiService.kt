package com.example

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeApiService {
    @GET("v6/{apiKey}/latest/{baseCurrency}")
    suspend fun getRates(
        @Path("apiKey") apiKey: String,
        @Path("baseCurrency") baseCurrency: String
    ): ExchangeRateResponse
}
