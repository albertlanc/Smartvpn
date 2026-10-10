package com.smartvpn.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_profiles")
data class VpnProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: ProfileType, // e.g., SSH, VLESS, VMESS, TROJAN, HYSTERIA2
    val serverAddress: String,
    val serverPort: Int,
    
    // Auth parameters
    val username: String? = null,
    val password: String? = null,
    val uuid: String? = null, // Used for Xray/Sing-box protocols
    
    // Protocol specific settings (TLS, SNI, Payload)
    val sni: String? = null,
    val customPayload: String? = null,
    val enableTls: Boolean = false,
    
    // DPI Bypass (TLS Fragmentation)
    val enableTlsFragmentation: Boolean = false,
    val fragmentSize: String = "50-100",
    val fragmentInterval: String = "10-20",
    
    // DNS Tunneling (DNSTT) specific
    val pubKey: String? = null,
    val nameserver: String? = null
)

enum class ProfileType {
    SSH, VLESS, VMESS, TROJAN, SHADOWSOCKS, HYSTERIA2, DNSTT
}
