package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgencyUiState
import com.example.ui.viewmodel.AgencyViewModel
import java.util.concurrent.TimeUnit

@Composable
fun ClientsScreen(
    state: AgencyUiState,
    viewModel: AgencyViewModel,
    onSelectClient: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<ClientStatus?>(null) }
    var selectedSourceFilter by remember { mutableStateOf<LeadSource?>(null) }
    var showAddClientDialog by remember { mutableStateOf(false) }

    val canManageClients = state.currentUser.role != UserRole.EMPLOYEE

    val filteredClients = remember(state.clients, searchQuery, selectedStatusFilter, selectedSourceFilter) {
        state.clients.filter { client ->
            val matchesQuery = searchQuery.isBlank() ||
                client.name.contains(searchQuery, ignoreCase = true) ||
                client.company.contains(searchQuery, ignoreCase = true) ||
                client.email.contains(searchQuery, ignoreCase = true)
            val matchesStatus = selectedStatusFilter == null || client.status == selectedStatusFilter
            val matchesSource = selectedSourceFilter == null || client.source == selectedSourceFilter
            matchesQuery && matchesStatus && matchesSource
        }
    }

    Scaffold(
        floatingActionButton = {
            if (canManageClients) {
                FloatingActionButton(
                    onClick = { showAddClientDialog = true },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_client_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Client")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search clients by name, company, email...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .testTag("client_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filters Scroll Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text("All Statuses (${state.clients.size})") }
                )
                ClientStatus.values().forEach { status ->
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = {
                            selectedStatusFilter = if (selectedStatusFilter == status) null else status
                        },
                        label = { Text(status.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredClients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.People,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No clients found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Try adjusting your search or status filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredClients, key = { it.id }) { client ->
                        val clientProjects = state.projects.filter { it.clientId == client.id }
                        val clientPayments = state.payments.filter { it.clientId == client.id }
                        val healthResult = viewModel.repository.calculateClientHealth(client, clientProjects, clientPayments)

                        ClientCardItem(
                            client = client,
                            health = healthResult.health,
                            healthReasons = healthResult.reasons,
                            activeProjectsCount = clientProjects.count { it.status == ProjectStatus.IN_PROGRESS },
                            onClick = { onSelectClient(client.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddClientDialog) {
        ClientEditorDialog(
            client = null,
            employees = state.employees,
            onDismiss = { showAddClientDialog = false },
            onSave = { newClient ->
                viewModel.saveClient(newClient)
                showAddClientDialog = false
            }
        )
    }
}

@Composable
fun ClientCardItem(
    client: Client,
    health: ClientHealth,
    healthReasons: List<String>,
    activeProjectsCount: Int,
    onClick: () -> Unit
) {
    val now = System.currentTimeMillis()
    val daysSinceContact = TimeUnit.MILLISECONDS.toDays(now - client.lastContactAt)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("client_card_${client.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = client.company,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = client.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                HealthBadge(health = health)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(
                        text = client.status.label,
                        color = when (client.status) {
                            ClientStatus.VIP -> Color(0xFFF59E0B)
                            ClientStatus.ACTIVE -> EmeraldSuccess
                            ClientStatus.PROSPECT -> CyanAccent
                            ClientStatus.LEAD -> IndigoLight
                            ClientStatus.INACTIVE -> Color(0xFF64748B)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Source: ${client.source.label}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "$activeProjectsCount Active Project${if (activeProjectsCount != 1) "s" else ""}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (health != ClientHealth.GOOD && healthReasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            (if (health == ClientHealth.AT_RISK) RoseError else AmberWarning).copy(alpha = 0.08f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (health == ClientHealth.AT_RISK) RoseError else AmberWarning
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = healthReasons.first(),
                        fontSize = 11.sp,
                        color = if (health == ClientHealth.AT_RISK) RoseError else AmberWarning,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ClientEditorDialog(
    client: Client?,
    employees: List<Employee>,
    onDismiss: () -> Unit,
    onSave: (Client) -> Unit
) {
    var name by remember { mutableStateOf(client?.name ?: "") }
    var company by remember { mutableStateOf(client?.company ?: "") }
    var email by remember { mutableStateOf(client?.email ?: "") }
    var phone by remember { mutableStateOf(client?.phone ?: "") }
    var whatsapp by remember { mutableStateOf(client?.whatsapp ?: "") }
    var instagram by remember { mutableStateOf(client?.instagram ?: "") }
    var address by remember { mutableStateOf(client?.address ?: "") }
    var notes by remember { mutableStateOf(client?.notes ?: "") }
    var status by remember { mutableStateOf(client?.status ?: ClientStatus.ACTIVE) }
    var source by remember { mutableStateOf(client?.source ?: LeadSource.GOOGLE) }
    var assignedEmployeeId by remember { mutableStateOf(client?.assignedEmployeeId ?: employees.firstOrNull()?.id ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (client == null) "New Client Profile" else "Edit Client Profile",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = company,
                        onValueChange = { company = it },
                        label = { Text("Company Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("client_input_company"),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Contact Person Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("client_input_name"),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth().testTag("client_input_email"),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = instagram,
                        onValueChange = { instagram = it },
                        label = { Text("Instagram Handle") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Office Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Internal Agency Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (company.isNotBlank() && name.isNotBlank()) {
                        val toSave = client?.copy(
                            name = name,
                            company = company,
                            email = email,
                            phone = phone,
                            whatsapp = whatsapp,
                            instagram = instagram,
                            address = address,
                            notes = notes,
                            status = status,
                            source = source,
                            assignedEmployeeId = assignedEmployeeId
                        ) ?: Client(
                            id = "cli_${System.currentTimeMillis()}",
                            name = name,
                            company = company,
                            email = email,
                            phone = phone,
                            whatsapp = whatsapp,
                            instagram = instagram,
                            address = address,
                            notes = notes,
                            status = status,
                            source = source,
                            assignedEmployeeId = assignedEmployeeId,
                            createdAt = System.currentTimeMillis(),
                            lastContactAt = System.currentTimeMillis()
                        )
                        onSave(toSave)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("save_client_button")
            ) {
                Text("Save Client")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
