package com.smartvpn

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartvpn.vpn.SmartVpnService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Launcher to request VPN permission from the Android OS
    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startSmartVpn()
        } else {
            Toast.makeText(this, "VPN Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onConnectClick = { checkAndStartVpn() },
                        onDisconnectClick = { stopSmartVpn() }
                    )
                }
            }
        }
    }

    private fun checkAndStartVpn() {
        // VpnService.prepare() returns an Intent if permission is needed, null if already granted
        val intent = VpnService.prepare(this)
        if (intent != null) {
            vpnPermissionLauncher.launch(intent)
        } else {
            startSmartVpn()
        }
    }

    private fun startSmartVpn() {
        val serviceIntent = Intent(this, SmartVpnService::class.java).apply {
            action = SmartVpnService.ACTION_START_VPN
            // Dummy config for testing the service start
            putExtra("CONFIG_JSON", "{}") 
            putExtra("BYPASS_LAN", true)
        }
        startService(serviceIntent)
    }

    private fun stopSmartVpn() {
        val serviceIntent = Intent(this, SmartVpnService::class.java).apply {
            action = SmartVpnService.ACTION_STOP_VPN
        }
        startService(serviceIntent)
    }
}

@Composable
fun MainScreen(onConnectClick: () -> Unit, onDisconnectClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Smart VPN", style = MaterialTheme.typography.headlineLarge)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onConnectClick, modifier = Modifier.fillMaxWidth(0.6f)) {
            Text("Connect")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(onClick = onDisconnectClick, modifier = Modifier.fillMaxWidth(0.6f)) {
            Text("Disconnect")
        }
    }
}
