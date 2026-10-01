package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import com.example.data.repository.DeadlineInfo
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgencyUiState
import com.example.ui.viewmodel.AgencyViewModel
import java.util.Locale

@Composable
fun ProjectsScreen(
    state: AgencyUiState,
    viewModel: AgencyViewModel,
    onSelectProject: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<ProjectStatus?>(null) }
    var selectedTypeFilter by remember { mutableStateOf<ProjectType?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val isEmployee = state.currentUser.role == UserRole.EMPLOYEE

    val userVisibleProjects = if (isEmployee) {
        state.projects.filter { it.assignedEmployeeIds.contains(state.currentUser.uid) }
    } else state.projects

    val filteredProjects = remember(userVisibleProjects, searchQuery, selectedStatusFilter, selectedTypeFilter) {
        userVisibleProjects.filter { project ->
            val matchesQuery = searchQuery.isBlank() ||
                project.name.contains(searchQuery, ignoreCase = true) ||
                project.notes.contains(searchQuery, ignoreCase = true)
            val matchesStatus = selectedStatusFilter == null || project.status == selectedStatusFilter
            val matchesType = selectedTypeFilter == null || project.type == selectedTypeFilter
            matchesQuery && matchesStatus && matchesType
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!isEmployee) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_project_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Project")
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
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search projects...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .testTag("project_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text("All (${userVisibleProjects.size})") }
                )
                ProjectStatus.values().forEach { status ->
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

            if (filteredProjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No projects found", style = MaterialTheme.typography.titleMedium)
                        Text("Try updating your search query or status filter", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredProjects, key = { it.projectId }) { project ->
                        val client = state.clients.find { it.id == project.clientId }
                        val deadlineInfo = viewModel.repository.formatDeadlineInfo(project.deadline, project.status)

                        ProjectCardItem(
                            project = project,
                            clientName = client?.company ?: "Agency Internal",
                            deadlineInfo = deadlineInfo,
                            showBudget = !isEmployee,
                            onClick = { onSelectProject(project.projectId) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ProjectEditorDialog(
            project = null,
            clients = state.clients,
            employees = state.employees,
            onDismiss = { showAddDialog = false },
            onSave = { newProj ->
                viewModel.saveProject(newProj)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ProjectCardItem(
    project: Project,
    clientName: String,
    deadlineInfo: DeadlineInfo,
    showBudget: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("project_card_${project.projectId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Client Name & Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = clientName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyanAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                PriorityBadge(priority = project.priority)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Project Title
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status & Type row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(
                        text = project.status.label,
                        color = when (project.status) {
                            ProjectStatus.COMPLETED, ProjectStatus.DELIVERED -> EmeraldSuccess
                            ProjectStatus.IN_PROGRESS -> IndigoLight
                            ProjectStatus.REVIEW, ProjectStatus.WAITING_CLIENT -> CyanAccent
                            ProjectStatus.DELAYED -> RoseError
                            ProjectStatus.CANCELLED -> Color(0xFF64748B)
                            else -> AmberWarning
                        }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = project.type.label,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (showBudget) {
                    Text(
                        text = "$${String.format(Locale.US, "%,.0f", project.budget)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${project.progress}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { project.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = when {
                    project.progress == 100 -> EmeraldSuccess
                    project.status == ProjectStatus.DELAYED -> RoseError
                    else -> IndigoPrimary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Deadline System row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DeadlineBadge(info = deadlineInfo)

                Text(
                    text = "Delivery target: ${project.agreedDeliveryTime}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ProjectEditorDialog(
    project: Project?,
    clients: List<Client>,
    employees: List<Employee>,
    onDismiss: () -> Unit,
    onSave: (Project) -> Unit
) {
    var name by remember { mutableStateOf(project?.name ?: "") }
    var selectedClientId by remember { mutableStateOf(project?.clientId ?: clients.firstOrNull()?.id ?: "") }
    var selectedType by remember { mutableStateOf(project?.type ?: ProjectType.WEBSITE) }
    var selectedPriority by remember { mutableStateOf(project?.priority ?: Priority.MEDIUM) }
    var selectedStatus by remember { mutableStateOf(project?.status ?: ProjectStatus.IN_PROGRESS) }
    var budgetStr by remember { mutableStateOf((project?.budget ?: 2000.0).toString()) }
    var progressStr by remember { mutableStateOf((project?.progress ?: 20).toString()) }
    var deliveryTime by remember { mutableStateOf(project?.agreedDeliveryTime ?: "18:00") }
    var notes by remember { mutableStateOf(project?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (project == null) "New Project" else "Edit Project",
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
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Project Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("project_name_input"),
                        singleLine = true
                    )
                }

                item {
                    Text("Client:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        clients.forEach { c ->
                            FilterChip(
                                selected = selectedClientId == c.id,
                                onClick = { selectedClientId = c.id },
                                label = { Text(c.company) }
                            )
                        }
                    }
                }

                item {
                    Text("Type:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ProjectType.values().forEach { t ->
                            FilterChip(
                                selected = selectedType == t,
                                onClick = { selectedType = t },
                                label = { Text(t.label) }
                            )
                        }
                    }
                }

                item {
                    Text("Priority:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Priority.values().forEach { p ->
                            FilterChip(
                                selected = selectedPriority == p,
                                onClick = { selectedPriority = p },
                                label = { Text(p.label) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = budgetStr,
                        onValueChange = { budgetStr = it },
                        label = { Text("Budget (USD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = progressStr,
                        onValueChange = { progressStr = it },
                        label = { Text("Progress Percentage (0-100)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = deliveryTime,
                        onValueChange = { deliveryTime = it },
                        label = { Text("Agreed Delivery Time (e.g. 18:00)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Project Brief & Requirements") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val budget = budgetStr.toDoubleOrNull() ?: 1500.0
                        val progress = (progressStr.toIntOrNull() ?: 0).coerceIn(0, 100)
                        val toSave = project?.copy(
                            name = name,
                            clientId = selectedClientId,
                            type = selectedType,
                            priority = selectedPriority,
                            status = selectedStatus,
                            budget = budget,
                            progress = progress,
                            agreedDeliveryTime = deliveryTime,
                            notes = notes,
                            updatedAt = System.currentTimeMillis()
                        ) ?: Project(
                            projectId = "proj_${System.currentTimeMillis()}",
                            name = name,
                            clientId = selectedClientId,
                            assignedEmployeeIds = listOf(employees.firstOrNull()?.id ?: "emp_1"),
                            type = selectedType,
                            priority = selectedPriority,
                            status = selectedStatus,
                            budget = budget,
                            progress = progress,
                            agreedDeliveryTime = deliveryTime,
                            notes = notes,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(toSave)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("save_project_button")
            ) {
                Text("Save Project")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
