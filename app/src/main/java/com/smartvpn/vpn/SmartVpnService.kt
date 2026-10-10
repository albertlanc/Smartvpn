package com.smartvpn.vpn

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.smartvpn.MainActivity
import com.smartvpn.SmartVpnApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class SmartVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    companion object {
        const val ACTION_START_VPN = "com.smartvpn.START_VPN"
        const val ACTION_STOP_VPN = "com.smartvpn.STOP_VPN"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_VPN -> {
                val configJson = intent.getStringExtra("CONFIG_JSON") ?: ""
                val allowedApps = intent.getStringArrayListExtra("ALLOWED_APPS") ?: arrayListOf()
                val bypassLan = intent.getBooleanExtra("BYPASS_LAN", true)
                
                startForegroundService()
                startVpnConnection(configJson, allowedApps, bypassLan)
            }
            ACTION_STOP_VPN -> {
                stopVpnConnection()
            }
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, SmartVpnApp.VPN_CHANNEL_ID)
            .setContentTitle("Smart VPN is active")
            .setContentText("Securing your network traffic")
            .setSmallIcon(android.R.drawable.ic_secure) // Placeholder icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun startVpnConnection(configJson: String, allowedApps: List<String>, bypassLan: Boolean) {
        try {
            val builder = Builder()
                .setSession("Smart VPN")
                .setMtu(1500)
                .addAddress("172.19.0.1", 30)
                // Default fallback IPv6 (Can be toggled via settings later)
                .addAddress("fdfe:dcba:9876::1", 126)

            // DNS Management
            builder.addDnsServer("1.1.1.1")
            builder.addDnsServer("1.0.0.1")

            // Network Routing & LAN Bypass
            if (bypassLan) {
                // Route public IPs, bypass local subnets (192.168.x.x, 10.x.x.x, 172.16.x.x)
                resources.getStringArray(android.R.array.emailAddressTypes) // Dummy workaround for brevity, real implementation requires exact IP CIDR blocks
                builder.addRoute("1.0.0.0", 8)
                builder.addRoute("2.0.0.0", 7)
                // In production, you will add all public IPv4 blocks here to bypass LAN.
                // For this foundational step, we route everything if bypassLan is false:
            } 
            
            // Route all traffic
            builder.addRoute("0.0.0.0", 0)
            builder.addRoute("::", 0) // Route all IPv6

            // Split Tunneling (App Filtering)
            if (allowedApps.isNotEmpty()) {
                for (appPackage in allowedApps) {
                    try {
                        builder.addAllowedApplication(appPackage)
                    } catch (e: Exception) {
                        Log.e("SmartVpn", "Package not found: $appPackage")
                    }
                }
            }

            // Establish the TUN interface
            vpnInterface = builder.establish()
            val fd = vpnInterface?.fd

            if (fd != null) {
                Log.d("SmartVpn", "VPN Interface established. FD: $fd")
                // TODO: Pass 'fd' and 'configJson' to the Sing-box/Xray Go engine here.
                // Example: SingboxWrapper.startEngine(configJson, fd)
            } else {
                Log.e("SmartVpn", "Failed to establish VPN interface (FD is null)")
                stopVpnConnection()
            }

        } catch (e: Exception) {
            Log.e("SmartVpn", "Error starting VPN: ${e.message}")
            stopVpnConnection()
        }
    }

    private fun stopVpnConnection() {
        // TODO: Stop the Sing-box/Xray Go engine here.
        // Example: SingboxWrapper.stopEngine()
        
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Log.e("SmartVpn", "Error closing VPN interface: ${e.message}")
        } finally {
            vpnInterface = null
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        stopVpnConnection()
        super.onDestroy()
    }
}
