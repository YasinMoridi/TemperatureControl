package com.yasinmoridi.temperaturecontrol.presentation.core.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasinmoridi.temperaturecontrol.data.dataStore.DataStoreRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DataStoreVM(
    private val repository: DataStoreRepository
) : ViewModel() {
    companion object{
        const val LOGIN_TOKEN = "login_token"
        const val USER_BALANCE = "balance"
    }

    var loginToken = mutableStateOf<String?>(null)
        private set //فقظ اینجا قابلیت تغییر دارد

    init {
        viewModelScope.launch {
            loginToken.value = repository.getString(LOGIN_TOKEN)
        }
    }

    fun saveUserBalance(balance: Int){
        viewModelScope.launch {
            repository.putInt(key = USER_BALANCE, value = balance)
        }
    }

    fun saveLoginToken(token: String){
        viewModelScope.launch {
            repository.putString(key = LOGIN_TOKEN, value = token)
        }
    }
}