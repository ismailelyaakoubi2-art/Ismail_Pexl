package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgencyUiState
import com.example.ui.viewmodel.AgencyViewModel

@Composable
fun SettingsScreen(
    state: AgencyUiState,
    viewModel: AgencyViewModel,
    onToggleTheme: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    var agencyName by remember(state.settings) { mutableStateOf(state.settings.agencyName) }
    var currency by remember(state.settings) { mutableStateOf(state.settings.currency) }
    var selectedLang by remember(state.settings) { mutableStateOf(state.settings.language) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    val isAdmin = state.currentUser.role == UserRole.ADMIN

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        // Firebase Cloud Backend Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isFirebaseReady) EmeraldSuccess.copy(alpha = 0.08f)
                                     else IndigoPrimary.copy(alpha = 0.08f)
                ),
                border = BorderStroke(
                    1.dp,
                    if (state.isFirebaseReady) EmeraldSuccess.copy(alpha = 0.4f)
                    else IndigoPrimary.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (state.isFirebaseReady) EmeraldSuccess else AmberWarning)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (state.isFirebaseReady) "Firebase Cloud Connected" else "Offline-First Local Mode",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        StatusBadge(
                            text = if (state.isFirebaseReady) "Firestore Active" else "Room Cache Ready",
                            color = if (state.isFirebaseReady) EmeraldSuccess else AmberWarning
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Real multi-user agency database with Cloud Firestore schema, Room local caching, and role-based Security Rules (ADMIN, MANAGER, EMPLOYEE).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showRulesDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("view_security_rules_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Firestore & Storage Security Rules", fontSize = 12.sp)
                    }
                }
            }
        }

        // Agency Branding
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Agency Profile", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

                    OutlinedTextField(
                        value = agencyName,
                        onValueChange = {
                            if (isAdmin) {
                                agencyName = it
                                viewModel.updateSettings(state.settings.copy(agencyName = it))
                            }
                        },
                        label = { Text("Agency Name") },
                        enabled = isAdmin,
                        modifier = Modifier.fillMaxWidth().testTag("settings_agency_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = currency,
                        onValueChange = {
                            if (isAdmin) {
                                currency = it
                                viewModel.updateSettings(state.settings.copy(currency = it))
                            }
                        },
                        label = { Text("Default Currency (e.g. USD, EUR, MAD)") },
                        enabled = isAdmin,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Language & RTL
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Language & Direction (RTL)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("Select your preferred agency dashboard language:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LanguageChip(
                            label = "English (US)",
                            code = "en",
                            isSelected = selectedLang == "en",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedLang = "en"
                            onLanguageChange("en")
                            viewModel.updateSettings(state.settings.copy(language = "en"))
                        }

                        LanguageChip(
                            label = "Français (FR)",
                            code = "fr",
                            isSelected = selectedLang == "fr",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedLang = "fr"
                            onLanguageChange("fr")
                            viewModel.updateSettings(state.settings.copy(language = "fr"))
                        }

                        LanguageChip(
                            label = "العربية (RTL)",
                            code = "ar",
                            isSelected = selectedLang == "ar",
                            modifier = Modifier.weight(1f)
                        ) {
                            selectedLang = "ar"
                            onLanguageChange("ar")
                            viewModel.updateSettings(state.settings.copy(language = "ar"))
                        }
                    }
                }
            }
        }

        // Appearance
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Dark Theme", fontWeight = FontWeight.Bold)
                            Text("Comfortable contrast for agency work", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = state.settings.isDarkMode,
                            onCheckedChange = { isDark ->
                                onToggleTheme(isDark)
                                viewModel.updateSettings(state.settings.copy(isDarkMode = isDark))
                            }
                        )
                    }
                }
            }
        }

        // Demo Data Reset
        if (isAdmin) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Demo Dataset & Reset", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reset all 10 clients, 6 employees, 12 projects, 20 tasks, and 10 payments to original clean initial agency state.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { showResetConfirm = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                            border = BorderStroke(1.dp, RoseError),
                            modifier = Modifier.testTag("reset_demo_data_btn")
                        ) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset Agency Demo Data")
                        }
                    }
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Demo Data?", fontWeight = FontWeight.Bold) },
            text = { Text("This will reload all 10 agency clients, 12 projects, 20 tasks, 10 payments, calendar events, and activity logs.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetDemoData()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                ) {
                    Text("Yes, Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showRulesDialog) {
        SecurityRulesViewerDialog(onDismiss = { showRulesDialog = false })
    }
}

@Composable
private fun LanguageChip(
    label: String,
    code: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .border(
                1.dp,
                if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(10.dp)
            ),
        color = if (isSelected) IndigoPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) IndigoLight else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SecurityRulesViewerDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = IndigoPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Firebase Security Rules", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    Text(
                        text = "Firestore Security Rules have been generated at /firestore.rules and Storage Rules at /storage.rules.\n\n" +
                               "Enforced Rules:\n" +
                               "• ADMIN: Full read/write on all collections.\n" +
                               "• MANAGER: Manage clients, projects, tasks, view payments.\n" +
                               "• EMPLOYEE: Access strictly restricted to assigned projects and tasks; financial payment documents blocked at security rule level.\n" +
                               "• Storage: Project and client documents protected with 50MB file size checks.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}
