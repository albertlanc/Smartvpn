package com.smartvpn.ui.profiles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartvpn.data.local.ProfileType
import com.smartvpn.data.local.VpnProfile
import com.smartvpn.data.local.VpnProfileDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProfileViewModel @Inject constructor(
    private val vpnProfileDao: VpnProfileDao
) : ViewModel() {

    var profileName by mutableStateOf("")
    var serverAddress by mutableStateOf("")
    var serverPort by mutableStateOf("443")
    var uuid by mutableStateOf("")
    var sni by mutableStateOf("")
    var selectedType by mutableStateOf(ProfileType.VLESS)
    var enableTls by mutableStateOf(true)

    fun saveProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val portInt = serverPort.toIntOrNull() ?: 443
            val newProfile = VpnProfile(
                name = profileName.ifEmpty { "New Profile" },
                type = selectedType,
                serverAddress = serverAddress,
                serverPort = portInt,
                uuid = uuid.ifEmpty { null },
                sni = sni.ifEmpty { null },
                enableTls = enableTls
            )
            
            vpnProfileDao.insertProfile(newProfile)
            onSuccess()
        }
    }
}
