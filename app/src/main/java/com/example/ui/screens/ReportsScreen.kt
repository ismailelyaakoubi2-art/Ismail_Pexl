package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgencyUiState
import com.example.ui.viewmodel.AgencyViewModel
import java.util.Locale

@Composable
fun ReportsScreen(
    state: AgencyUiState,
    viewModel: AgencyViewModel
) {
    val context = LocalContext.current
    val isEmployee = state.currentUser.role == UserRole.EMPLOYEE

    val totalProjects = state.projects.size
    val completedProjects = state.projects.count { it.status == ProjectStatus.COMPLETED || it.status == ProjectStatus.DELIVERED }
    val delayedProjects = state.projects.count { it.status == ProjectStatus.DELAYED }
    val completionRate = if (totalProjects > 0) (completedProjects.toFloat() / totalProjects * 100).toInt() else 0

    val totalRevenue = if (!isEmployee) state.payments.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount } else 0.0
    val totalPending = if (!isEmployee) state.payments.filter { it.status == PaymentStatus.PENDING }.sumOf { it.amount } else 0.0

    val totalTasks = state.tasks.size
    val doneTasks = state.tasks.count { it.status == TaskStatus.DONE }
    val taskRate = if (totalTasks > 0) (doneTasks.toFloat() / totalTasks * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        // Header & CSV Export
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Agency Performance Reports",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = {
                        val csv = generateAgencyCsv(state)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, csv)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Export Agency Report"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("export_csv_btn")
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV", fontSize = 12.sp)
                }
            }
        }

        // Summary cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Project On-Time Rate",
                    value = "$completionRate%",
                    subtitle = "$completedProjects Completed / $totalProjects Total",
                    icon = Icons.Default.Verified,
                    accentColor = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Task Velocity",
                    value = "$taskRate%",
                    subtitle = "$doneTasks of $totalTasks Tasks Done",
                    icon = Icons.Default.Checklist,
                    accentColor = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (!isEmployee) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Revenue Realization", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Collected Revenue", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${String.format(Locale.US, "%,.2f", totalRevenue)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EmeraldSuccess)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Pending Receivables", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${String.format(Locale.US, "%,.2f", totalPending)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AmberWarning)
                            }
                        }
                    }
                }
            }
        }

        // Employee Workload Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Team Capacity & Performance", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(10.dp))

                    state.employees.forEach { emp ->
                        val empTasks = state.tasks.filter { it.assignedEmployeeId == emp.id }
                        val empDone = empTasks.count { it.status == TaskStatus.DONE }
                        val empActiveProjects = state.projects.count { it.assignedEmployeeIds.contains(emp.id) && it.status == ProjectStatus.IN_PROGRESS }

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(emp.fullName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("$empActiveProjects Active Proj • $empDone/${empTasks.size} Tasks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            val ratio = if (empTasks.isNotEmpty()) empDone.toFloat() / empTasks.size else 0f
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = IndigoPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun generateAgencyCsv(state: AgencyUiState): String {
    val sb = StringBuilder()
    sb.append("Agency Management Performance Report\n")
    sb.append("Agency: ${state.settings.agencyName}\n\n")

    sb.append("CLIENTS\n")
    sb.append("ID,Name,Company,Email,Status,Source\n")
    state.clients.forEach { c ->
        sb.append("${c.id},\"${c.name}\",\"${c.company}\",\"${c.email}\",${c.status.name},${c.source.name}\n")
    }

    sb.append("\nPROJECTS\n")
    sb.append("ID,Name,Status,Priority,Progress,Budget\n")
    state.projects.forEach { p ->
        sb.append("${p.projectId},\"${p.name}\",${p.status.name},${p.priority.name},${p.progress}%,${p.budget}\n")
    }

    sb.append("\nTASKS\n")
    sb.append("ID,Title,Status,Priority,Due Date\n")
    state.tasks.forEach { t ->
        sb.append("${t.id},\"${t.title}\",${t.status.name},${t.priority.name},${t.dueDate}\n")
    }

    sb.append("\nPAYMENTS\n")
    sb.append("ID,Amount,Currency,Status,Method\n")
    state.payments.forEach { pay ->
        sb.append("${pay.id},${pay.amount},${pay.currency},${pay.status.name},${pay.paymentMethod.name}\n")
    }

    return sb.toString()
}
