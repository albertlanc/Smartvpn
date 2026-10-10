package com.smartvpn.ui.profiles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileScreen(
    viewModel: AddProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add VLess Profile") },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.profileName,
                onValueChange = { viewModel.profileName = it },
                label = { Text("Profile Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.serverAddress,
                onValueChange = { viewModel.serverAddress = it },
                label = { Text("Server Address (IP or Domain)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.serverPort,
                onValueChange = { viewModel.serverPort = it },
                label = { Text("Port (e.g., 443)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.uuid,
                onValueChange = { viewModel.uuid = it },
                label = { Text("UUID") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = viewModel.sni,
                onValueChange = { viewModel.sni = it },
                label = { Text("SNI (Server Name Indication)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = viewModel.enableTls,
                    onCheckedChange = { viewModel.enableTls = it }
                )
                Text("Enable TLS")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveProfile(onSuccess = { onNavigateBack() })
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Profile")
            }
        }
    }
}
