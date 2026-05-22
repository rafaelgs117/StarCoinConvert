package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class MainViewModel : ViewModel() {
    private val _apiKey = MutableStateFlow("e0bd6e667ba74d1c9a9969fc")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates.asStateFlow()

    private val _lastUpdate = MutableStateFlow("")
    val lastUpdate: StateFlow<String> = _lastUpdate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentBase = "USD"

    init {
        startAutoRefresh()
    }

    fun updateApiKey(key: String) {
        _apiKey.value = key
        fetchRates(currentBase)
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                fetchRates(currentBase)
                delay(60 * 60 * 1000L) // Refresh once per hour
            }
        }
    }

    fun fetchRates(base: String) {
        currentBase = base
        if (_apiKey.value.isBlank()) {
            _error.value = "Insira uma chave de API válida para buscar as taxas."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.api.getRates(_apiKey.value, base)
                if (response.result == "success") {
                    _rates.value = response.conversion_rates
                    _lastUpdate.value = formatDate(response.time_last_update_utc)
                } else {
                    _error.value = "Erro ao buscar taxas."
                }
            } catch (e: UnknownHostException) {
                _error.value = "Sem conexão com a internet."
            } catch (e: Exception) {
                _error.value = "Erro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun convert(amount: Double, toCurrency: String): Double {
        val ratesMap = _rates.value
        val rate = ratesMap[toCurrency] ?: 0.0
        return amount * rate
    }

    fun getRate(currency: String): Double {
        return _rates.value[currency] ?: 0.0
    }

    private fun formatDate(raw: String): String = try {
        // e.g. "Fri, 22 May 2026 00:00:01 +0000"
        val parts = raw.split(", ", " ")
        if (parts.size >= 5) {
            "${parts[1]} ${parts[2]} ${parts[3]} às ${parts[4].substring(0, 5)}"
        } else raw
    } catch (e: Exception) {
        raw
    }
}
