package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    state: AgencyUiState,
    viewModel: AgencyViewModel,
    onBack: () -> Unit,
    onOpenClient: (String) -> Unit
) {
    val project = state.projects.find { it.projectId == projectId }

    if (project == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Project not found")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) { Text("Go Back") }
            }
        }
        return
    }

    val client = state.clients.find { it.id == project.clientId }
    val projectTasks = state.tasks.filter { it.projectId == projectId }
    val assignedEmployees = state.employees.filter { project.assignedEmployeeIds.contains(it.id) }
    val deadlineInfo = viewModel.repository.formatDeadlineInfo(project.deadline, project.status)

    val isEmployee = state.currentUser.role == UserRole.EMPLOYEE
    val canDeliver = !isEmployee && project.status != ProjectStatus.DELIVERED && project.status != ProjectStatus.COMPLETED

    var showDeliverDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project.name, fontWeight = FontWeight.Bold, maxLines = 1) },
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
                            viewModel.deleteProject(project)
                            onBack()
                        }) {
                            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = RoseError)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (canDeliver) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { showDeliverDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("deliver_project_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark Project as Delivered", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // Header Status & Deadline Card
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
                            StatusBadge(
                                text = project.status.label,
                                color = when (project.status) {
                                    ProjectStatus.DELIVERED, ProjectStatus.COMPLETED -> EmeraldSuccess
                                    ProjectStatus.DELAYED -> RoseError
                                    else -> IndigoPrimary
                                }
                            )
                            PriorityBadge(priority = project.priority)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = project.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dynamic Deadline System
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Deadline Countdown", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                DeadlineBadge(info = deadlineInfo)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Target Date", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${dateFormat.format(Date(project.deadline))} @ ${project.agreedDeliveryTime}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Overall Completion", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${project.progress}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { project.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = IndigoPrimary
                        )
                    }
                }
            }

            // Delivery Banner (if delivered)
            if (project.status == ProjectStatus.DELIVERED || project.status == ProjectStatus.COMPLETED) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSuccess.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delivery Confirmed", fontWeight = FontWeight.Bold, color = EmeraldSuccess)
                            }
                            if (project.deliveredAt != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Delivered on: ${dateFormat.format(Date(project.deliveredAt))} by ${project.deliveredBy ?: "Agency"}",
                                    fontSize = 12.sp
                                )
                            }
                            if (!project.deliveryNotes.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notes: ${project.deliveryNotes}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Client Info Card
            if (client != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenClient(client.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Client", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(client.company, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${client.name} • ${client.phone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Assigned Team
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Assigned Team Members", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        if (assignedEmployees.isEmpty()) {
                            Text("No team members assigned.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            assignedEmployees.forEach { emp ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(emp.avatarColor)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = emp.fullName.take(2).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(emp.fullName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(emp.position.title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tasks List
            item {
                Text(
                    text = "Project Tasks (${projectTasks.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(projectTasks) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(task.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(task.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                        StatusBadge(
                            text = task.status.label,
                            color = when (task.status) {
                                TaskStatus.DONE -> EmeraldSuccess
                                TaskStatus.IN_PROGRESS -> IndigoLight
                                TaskStatus.REVIEW -> CyanAccent
                                TaskStatus.TODO -> Color(0xFF64748B)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeliverDialog) {
        var deliveryNotes by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDeliverDialog = false },
            title = { Text("Deliver Project to Client", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This marks the project as 100% completed and logs the official delivery record in the global activity timeline.")
                    OutlinedTextField(
                        value = deliveryNotes,
                        onValueChange = { deliveryNotes = it },
                        label = { Text("Delivery notes / Link to drive") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deliverProject(project, deliveryNotes)
                        showDeliverDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Text("Confirm Delivery")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeliverDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showEditDialog) {
        ProjectEditorDialog(
            project = project,
            clients = state.clients,
            employees = state.employees,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.saveProject(updated)
                showEditDialog = false
            }
        )
    }
}
