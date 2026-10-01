package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TasksScreen(
    state: AgencyUiState,
    viewModel: AgencyViewModel
) {
    val isEmployee = state.currentUser.role == UserRole.EMPLOYEE

    // Role-based visible tasks
    val visibleTasks = if (isEmployee) {
        state.tasks.filter { it.assignedEmployeeId == state.currentUser.uid }
    } else state.tasks

    var selectedPriorityFilter by remember { mutableStateOf<Priority?>(null) }
    var selectedColumnIndex by remember { mutableIntStateOf(0) }
    val kanbanColumns = listOf(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.REVIEW, TaskStatus.DONE)
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val filteredTasks = remember(visibleTasks, selectedPriorityFilter) {
        visibleTasks.filter { task ->
            selectedPriorityFilter == null || task.priority == selectedPriorityFilter
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!isEmployee) {
                FloatingActionButton(
                    onClick = { showAddTaskDialog = true },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_task_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Task")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Priority Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedPriorityFilter == null,
                    onClick = { selectedPriorityFilter = null },
                    label = { Text("All Priorities") }
                )
                Priority.values().forEach { prio ->
                    FilterChip(
                        selected = selectedPriorityFilter == prio,
                        onClick = {
                            selectedPriorityFilter = if (selectedPriorityFilter == prio) null else prio
                        },
                        label = { Text(prio.label) }
                    )
                }
            }

            // Kanban Column Selector Tabs
            TabRow(
                selectedTabIndex = selectedColumnIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                kanbanColumns.forEachIndexed { index, status ->
                    val count = filteredTasks.count { it.status == status }
                    Tab(
                        selected = selectedColumnIndex == index,
                        onClick = { selectedColumnIndex = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(status.label, fontSize = 13.sp, fontWeight = if (selectedColumnIndex == index) FontWeight.Bold else FontWeight.Normal)
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(if (selectedColumnIndex == index) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$count",
                                        fontSize = 10.sp,
                                        color = if (selectedColumnIndex == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // Tasks in current Kanban column
            val currentStatus = kanbanColumns[selectedColumnIndex]
            val tasksInColumn = filteredTasks.filter { it.status == currentStatus }

            if (tasksInColumn.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Checklist,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No tasks in ${currentStatus.label}", style = MaterialTheme.typography.titleMedium)
                        Text("Move tasks here from another status or add new ones.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(tasksInColumn, key = { it.id }) { task ->
                        val project = state.projects.find { it.projectId == task.projectId }
                        val assignee = state.employees.find { it.id == task.assignedEmployeeId }

                        KanbanTaskCard(
                            task = task,
                            projectName = project?.name ?: "General Task",
                            assignee = assignee,
                            onMoveStatus = { next -> viewModel.moveTaskStatus(task, next) },
                            onDelete = { viewModel.deleteTask(task) },
                            canDelete = !isEmployee
                        )
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        TaskEditorDialog(
            projects = state.projects,
            employees = state.employees,
            onDismiss = { showAddTaskDialog = false },
            onSave = { newTask ->
                viewModel.saveTask(newTask)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun KanbanTaskCard(
    task: Task,
    projectName: String,
    assignee: Employee?,
    onMoveStatus: (TaskStatus) -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }
    val now = System.currentTimeMillis()
    val isOverdue = task.status != TaskStatus.DONE && task.dueDate < now

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Project Title & Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = projectName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CyanAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                PriorityBadge(priority = task.priority)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Task Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Assignee & Due Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (assignee != null) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(assignee.avatarColor)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = assignee.fullName.take(2).uppercase(),
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = assignee.fullName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "Unassigned",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isOverdue) Icons.Default.Warning else Icons.Outlined.Event,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = if (isOverdue) RoseError else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = (if (isOverdue) "Overdue " else "") + dateFormat.format(Date(task.dueDate)),
                        fontSize = 11.sp,
                        fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal,
                        color = if (isOverdue) RoseError else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Advance Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TaskStatus.values().forEach { st ->
                        val isCurrent = task.status == st
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { if (!isCurrent) onMoveStatus(st) }
                                .then(
                                    if (isCurrent) Modifier.border(1.dp, IndigoPrimary, RoundedCornerShape(8.dp))
                                    else Modifier
                                ),
                            color = if (isCurrent) IndigoPrimary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = st.label,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) IndigoLight else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskEditorDialog(
    projects: List<Project>,
    employees: List<Employee>,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedProjectId by remember { mutableStateOf(projects.firstOrNull()?.projectId ?: "") }
    var selectedAssigneeId by remember { mutableStateOf(employees.firstOrNull()?.id ?: "") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Task", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title *") },
                        modifier = Modifier.fillMaxWidth().testTag("task_title_input"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Task Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    Text("Related Project:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        projects.forEach { p ->
                            FilterChip(
                                selected = selectedProjectId == p.projectId,
                                onClick = { selectedProjectId = p.projectId },
                                label = { Text(p.name, maxLines = 1) }
                            )
                        }
                    }
                }

                item {
                    Text("Assign To:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        employees.forEach { emp ->
                            FilterChip(
                                selected = selectedAssigneeId == emp.id,
                                onClick = { selectedAssigneeId = emp.id },
                                label = { Text(emp.fullName) }
                            )
                        }
                    }
                }

                item {
                    Text("Priority:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Priority.values().forEach { prio ->
                            FilterChip(
                                selected = selectedPriority == prio,
                                onClick = { selectedPriority = prio },
                                label = { Text(prio.label) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val task = Task(
                            id = "tsk_${System.currentTimeMillis()}",
                            title = title,
                            description = description,
                            projectId = selectedProjectId,
                            clientId = projects.find { it.projectId == selectedProjectId }?.clientId ?: "",
                            assignedEmployeeId = selectedAssigneeId,
                            priority = selectedPriority,
                            dueDate = System.currentTimeMillis() + 3 * 86400000L,
                            status = TaskStatus.TODO,
                            createdAt = System.currentTimeMillis()
                        )
                        onSave(task)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text("Create Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
