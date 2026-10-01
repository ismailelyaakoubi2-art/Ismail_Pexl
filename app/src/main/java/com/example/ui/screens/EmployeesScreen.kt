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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgencyUiState
import com.example.ui.viewmodel.AgencyViewModel

@Composable
fun EmployeesScreen(
    state: AgencyUiState,
    viewModel: AgencyViewModel
) {
    val canManage = state.currentUser.role == UserRole.ADMIN || state.currentUser.role == UserRole.MANAGER
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (canManage) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = IndigoPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_employee_fab")
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Team Member")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
        ) {
            items(state.employees, key = { it.id }) { emp ->
                val assignedProjects = state.projects.count { it.assignedEmployeeIds.contains(emp.id) }
                val assignedTasks = state.tasks.count { it.assignedEmployeeId == emp.id }
                val completedTasks = state.tasks.count { it.assignedEmployeeId == emp.id && it.status == TaskStatus.DONE }

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("employee_card_${emp.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(emp.avatarColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = emp.fullName.take(2).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = emp.fullName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = emp.position.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CyanAccent
                                    )
                                }
                            }

                            StatusBadge(
                                text = emp.status.label,
                                color = when (emp.status) {
                                    UserStatus.ACTIVE -> EmeraldSuccess
                                    UserStatus.AWAY -> AmberWarning
                                    UserStatus.INACTIVE -> Color(0xFF64748B)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = emp.email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            StatusBadge(text = emp.role.name, color = IndigoLight)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Stats metrics
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$assignedProjects", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Projects", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$assignedTasks", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Tasks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$completedTasks", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EmeraldSuccess)
                                Text("Completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        EmployeeEditorDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newEmp ->
                viewModel.saveEmployee(newEmp)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun EmployeeEditorDialog(
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var position by remember { mutableStateOf(UserPosition.DESIGNER) }
    var role by remember { mutableStateOf(UserRole.EMPLOYEE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Team Member", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("employee_name_input"),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email *") },
                        modifier = Modifier.fillMaxWidth(),
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
                    Text("Role:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UserRole.values().forEach { r ->
                            FilterChip(
                                selected = role == r,
                                onClick = { role = r },
                                label = { Text(r.name) }
                            )
                        }
                    }
                }
                item {
                    Text("Position:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UserPosition.values().forEach { pos ->
                            FilterChip(
                                selected = position == pos,
                                onClick = { position = pos },
                                label = { Text(pos.title) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank()) {
                        val emp = Employee(
                            id = "emp_${System.currentTimeMillis()}",
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            role = role,
                            position = position,
                            status = UserStatus.ACTIVE,
                            avatarColor = 0xFF6366F1
                        )
                        onSave(emp)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("save_employee_button")
            ) {
                Text("Add Member")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
