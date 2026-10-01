package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailScreen(
    clientId: String,
    state: AgencyUiState,
    viewModel: AgencyViewModel,
    onBack: () -> Unit,
    onOpenProject: (String) -> Unit
) {
    val client = state.clients.find { it.id == clientId }

    if (client == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Client not found")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) { Text("Go Back") }
            }
        }
        return
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Projects", "Payments", "Communication", "Notes", "Files", "Reminders")

    var showEditDialog by remember { mutableStateOf(false) }
    var showAddCommDialog by remember { mutableStateOf(false) }
    var showAddFileDialog by remember { mutableStateOf(false) }

    val clientProjects = state.projects.filter { it.clientId == clientId }
    val clientPayments = state.payments.filter { it.clientId == clientId }
    val clientLogs = state.communicationLogs.filter { it.clientId == clientId }
    val clientReminders = state.reminders.filter { it.relatedClientId == clientId }
    val clientFiles = remember(state.projects) {
        // Collect files for client
        emptyList<ProjectFile>() // Will query from repository or file list
    }

    val healthResult = viewModel.repository.calculateClientHealth(client, clientProjects, clientPayments)
    val isEmployee = state.currentUser.role == UserRole.EMPLOYEE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(client.company, fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEmployee) {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = {
                            viewModel.deleteClient(client)
                            onBack()
                        }) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = RoseError)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Health & Client Header Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                            Text(
                                text = client.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${client.email} • ${client.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        HealthBadge(health = healthResult.health)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Transparent Health Reasons
                    Text(
                        text = "Client Health Analysis:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    healthResult.reasons.forEach { reason ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (healthResult.health) {
                                            ClientHealth.GOOD -> EmeraldSuccess
                                            ClientHealth.ATTENTION -> AmberWarning
                                            ClientHealth.AT_RISK -> RoseError
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = reason,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> ClientOverviewTab(client = client, projects = clientProjects, payments = clientPayments)
                    1 -> ClientProjectsTab(projects = clientProjects, onOpenProject = onOpenProject)
                    2 -> ClientPaymentsTab(payments = clientPayments, isEmployee = isEmployee)
                    3 -> ClientCommunicationTab(
                        clientId = clientId,
                        logs = clientLogs,
                        onAddLog = { showAddCommDialog = true }
                    )
                    4 -> ClientNotesTab(client = client)
                    5 -> ClientFilesTab(files = clientFiles, onUpload = { showAddFileDialog = true })
                    6 -> ClientRemindersTab(reminders = clientReminders, onToggle = { viewModel.toggleReminder(it) })
                }
            }
        }
    }

    if (showEditDialog) {
        ClientEditorDialog(
            client = client,
            employees = state.employees,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.saveClient(updated)
                showEditDialog = false
            }
        )
    }

    if (showAddCommDialog) {
        AddCommunicationDialog(
            onDismiss = { showAddCommDialog = false },
            onSave = { type, note ->
                viewModel.addCommunicationLog(client.id, type, note)
                showAddCommDialog = false
            }
        )
    }
}

@Composable
private fun ClientOverviewTab(
    client: Client,
    projects: List<Project>,
    payments: List<Payment>
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Contact Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    DetailRow(label = "Company", value = client.company)
                    DetailRow(label = "Contact Person", value = client.name)
                    DetailRow(label = "Email", value = client.email.ifBlank { "Not specified" })
                    DetailRow(label = "Phone", value = client.phone.ifBlank { "Not specified" })
                    DetailRow(label = "WhatsApp", value = client.whatsapp.ifBlank { "Not specified" })
                    DetailRow(label = "Instagram", value = client.instagram.ifBlank { "Not specified" })
                    DetailRow(label = "Address", value = client.address.ifBlank { "Not specified" })
                    DetailRow(label = "Lead Source", value = client.source.label)
                    DetailRow(label = "Status", value = client.status.label)
                }
            }
        }
    }
}

@Composable
private fun ClientProjectsTab(
    projects: List<Project>,
    onOpenProject: (String) -> Unit
) {
    if (projects.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No projects for this client yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(projects) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProject(project.projectId) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(project.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            StatusBadge(text = project.status.label, color = IndigoPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { project.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = IndigoPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientPaymentsTab(
    payments: List<Payment>,
    isEmployee: Boolean
) {
    if (isEmployee) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Financial payment records are restricted to Admins & Managers.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else if (payments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No payments recorded for this client.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(payments) { payment ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$${String.format(Locale.US, "%,.2f", payment.amount)} ${payment.currency}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Method: ${payment.paymentMethod.label}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(
                            text = payment.status.label,
                            color = when (payment.status) {
                                PaymentStatus.PAID -> EmeraldSuccess
                                PaymentStatus.PENDING -> AmberWarning
                                PaymentStatus.OVERDUE -> RoseError
                                PaymentStatus.PARTIALLY_PAID -> CyanAccent
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientCommunicationTab(
    clientId: String,
    logs: List<CommunicationLog>,
    onAddLog: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onAddLog,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.AddComment, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Communication")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No communication logs yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(logs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(text = log.type.label, color = CyanAccent)
                                Text(
                                    text = dateFormat.format(Date(log.dateMillis)),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = log.note, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientNotesTab(client: Client) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Internal Client Notes", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = client.notes.ifBlank { "No special notes recorded." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ClientFilesTab(files: List<ProjectFile>, onUpload: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onUpload,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.UploadFile, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upload File to Storage")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Client Files & Briefs", fontWeight = FontWeight.Medium)
                Text("Stored securely in Firebase Cloud Storage", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ClientRemindersTab(reminders: List<Reminder>, onToggle: (Reminder) -> Unit) {
    if (reminders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No reminders scheduled for this client.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(reminders) { rem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = rem.completed, onCheckedChange = { onToggle(rem) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(rem.title, fontWeight = FontWeight.SemiBold)
                            Text(rem.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

@Composable
fun AddCommunicationDialog(
    onDismiss: () -> Unit,
    onSave: (CommunicationType, String) -> Unit
) {
    var selectedType by remember { mutableStateOf(CommunicationType.PHONE) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Client Communication", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Communication Channel:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CommunicationType.values().take(3).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.label, fontSize = 11.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CommunicationType.values().drop(3).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.label, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Meeting / Conversation Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (note.isNotBlank()) {
                        onSave(selectedType, note)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Log Communication")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
