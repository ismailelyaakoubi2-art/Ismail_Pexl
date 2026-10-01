package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import java.util.concurrent.TimeUnit

@Composable
fun DashboardScreen(
    state: AgencyUiState,
    viewModel: AgencyViewModel,
    onNavigate: (String) -> Unit,
    onOpenClient: (String) -> Unit,
    onOpenProject: (String) -> Unit,
    onQuickAction: (String) -> Unit
) {
    val isEmployee = state.currentUser.role == UserRole.EMPLOYEE

    // Filter projects and tasks if user is restricted Employee
    val visibleProjects = if (isEmployee) {
        state.projects.filter { it.assignedEmployeeIds.contains(state.currentUser.uid) }
    } else state.projects

    val visibleTasks = if (isEmployee) {
        state.tasks.filter { it.assignedEmployeeId == state.currentUser.uid }
    } else state.tasks

    // Top metrics calculations
    val totalClients = state.clients.size
    val activeClients = state.clients.count { it.status == ClientStatus.ACTIVE || it.status == ClientStatus.VIP }
    val totalProjects = visibleProjects.size
    val activeProjects = visibleProjects.count { it.status == ProjectStatus.IN_PROGRESS }
    val completedProjects = visibleProjects.count { it.status == ProjectStatus.COMPLETED || it.status == ProjectStatus.DELIVERED }
    val delayedProjects = visibleProjects.count { it.status == ProjectStatus.DELAYED }
    val openTasks = visibleTasks.count { it.status != TaskStatus.DONE }
    val completedTasks = visibleTasks.count { it.status == TaskStatus.DONE }

    // Revenue calculations (hidden for regular employees)
    val totalRevenue = if (!isEmployee) state.payments.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount } else 0.0
    val pendingRevenue = if (!isEmployee) state.payments.filter { it.status == PaymentStatus.PENDING || it.status == PaymentStatus.OVERDUE }.sumOf { it.amount } else 0.0

    // Needs Attention calculations
    val now = System.currentTimeMillis()
    val overdueProjects = visibleProjects.filter {
        it.status != ProjectStatus.COMPLETED &&
        it.status != ProjectStatus.DELIVERED &&
        it.status != ProjectStatus.CANCELLED &&
        it.deadline < now
    }
    val dueSoonProjects = visibleProjects.filter {
        it.status != ProjectStatus.COMPLETED &&
        it.status != ProjectStatus.DELIVERED &&
        it.deadline >= now &&
        it.deadline <= now + 48 * 3600000L
    }
    val overdueTasks = visibleTasks.filter {
        it.status != TaskStatus.DONE && it.dueDate < now
    }
    val overduePayments = if (!isEmployee) state.payments.filter { it.status == PaymentStatus.OVERDUE } else emptyList()
    val staleClients = state.clients.filter {
        TimeUnit.MILLISECONDS.toDays(now - it.lastContactAt) > 14
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Welcome Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = IndigoPrimary
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome back, ${state.currentUser.fullName}!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${state.settings.agencyName} • ${state.currentUser.role.name} View",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Column {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        QuickActionPill(
                            title = "New Client",
                            icon = Icons.Default.PersonAdd,
                            color = IndigoPrimary
                        ) { onQuickAction("client") }
                    }
                    item {
                        QuickActionPill(
                            title = "New Project",
                            icon = Icons.Default.WorkOutline,
                            color = CyanAccent
                        ) { onQuickAction("project") }
                    }
                    item {
                        QuickActionPill(
                            title = "New Task",
                            icon = Icons.Default.AddTask,
                            color = PurpleAccent
                        ) { onQuickAction("task") }
                    }
                    if (!isEmployee) {
                        item {
                            QuickActionPill(
                                title = "New Payment",
                                icon = Icons.Default.AttachMoney,
                                color = EmeraldSuccess
                            ) { onQuickAction("payment") }
                        }
                    }
                    item {
                        QuickActionPill(
                            title = "New Reminder",
                            icon = Icons.Default.NotificationsActive,
                            color = AmberWarning
                        ) { onQuickAction("reminder") }
                    }
                }
            }
        }

        // Top Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Key Metrics",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Active Projects",
                        value = "$activeProjects",
                        subtitle = "$totalProjects Total",
                        icon = Icons.Default.Folder,
                        accentColor = IndigoPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("projects") }
                    )
                    StatCard(
                        title = "Open Tasks",
                        value = "$openTasks",
                        subtitle = "$completedTasks Done",
                        icon = Icons.Default.Checklist,
                        accentColor = CyanAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("tasks") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Clients",
                        value = "$activeClients",
                        subtitle = "$totalClients Total",
                        icon = Icons.Default.Business,
                        accentColor = PurpleAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate("clients") }
                    )
                    if (!isEmployee) {
                        StatCard(
                            title = "Revenue",
                            value = "$${String.format(Locale.US, "%,.0f", totalRevenue)}",
                            subtitle = "$${String.format(Locale.US, "%,.0f", pendingRevenue)} Pending",
                            icon = Icons.Default.AttachMoney,
                            accentColor = EmeraldSuccess,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("payments") }
                        )
                    } else {
                        StatCard(
                            title = "Delayed Projects",
                            value = "$delayedProjects",
                            subtitle = "Need Reschedule",
                            icon = Icons.Default.Warning,
                            accentColor = RoseError,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate("projects") }
                        )
                    }
                }
            }
        }

        // "Needs Attention" Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, RoseError.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationImportant,
                                contentDescription = null,
                                tint = RoseError,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Needs Attention",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        val totalIssues = overdueProjects.size + dueSoonProjects.size + overdueTasks.size + overduePayments.size + staleClients.size
                        Badge(containerColor = RoseError, contentColor = Color.White) {
                            Text("$totalIssues")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (overdueProjects.isEmpty() && dueSoonProjects.isEmpty() && overdueTasks.isEmpty() && overduePayments.isEmpty() && staleClients.isEmpty()) {
                        Text(
                            text = "🎉 Awesome! Everything is running smoothly with no overdue items.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            overdueProjects.forEach { proj ->
                                AttentionRow(
                                    title = proj.name,
                                    subtitle = "Project overdue! Target deadline missed.",
                                    type = "Overdue Project",
                                    color = RoseError,
                                    onClick = { onOpenProject(proj.projectId) }
                                )
                            }
                            dueSoonProjects.forEach { proj ->
                                AttentionRow(
                                    title = proj.name,
                                    subtitle = "Due within 48 hours. Ready for final review.",
                                    type = "Due Soon",
                                    color = AmberWarning,
                                    onClick = { onOpenProject(proj.projectId) }
                                )
                            }
                            overdueTasks.take(3).forEach { task ->
                                AttentionRow(
                                    title = task.title,
                                    subtitle = "Assigned task past due date.",
                                    type = "Overdue Task",
                                    color = RoseError,
                                    onClick = { onNavigate("tasks") }
                                )
                            }
                            overduePayments.forEach { pay ->
                                AttentionRow(
                                    title = "Invoice $${String.format(Locale.US, "%.0f", pay.amount)} ${pay.currency}",
                                    subtitle = "Payment overdue from client.",
                                    type = "Unpaid Invoice",
                                    color = AmberWarning,
                                    onClick = { onNavigate("payments") }
                                )
                            }
                            staleClients.take(2).forEach { client ->
                                AttentionRow(
                                    title = client.company,
                                    subtitle = "No communication in > 14 days.",
                                    type = "Stale Client",
                                    color = CyanAccent,
                                    onClick = { onOpenClient(client.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Charts
        item {
            ProjectsStatusDonutChart(projects = visibleProjects)
        }

        if (!isEmployee) {
            item {
                val monthlyData = listOf(
                    "Jun" to 9500.0,
                    "Jul" to 12400.0,
                    "Aug" to 15800.0,
                    "Sep" to 18600.0,
                    "Oct" to 22400.0
                )
                RevenueBarChart(monthlyData = monthlyData)
            }
        }

        item {
            LeadSourceDistribution(clients = state.clients)
        }
    }
}

@Composable
private fun QuickActionPill(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("quick_action_${title.lowercase().replace(" ", "_")}"),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AttentionRow(
    title: String,
    subtitle: String,
    type: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(8.dp))
        StatusBadge(text = type, color = color)
    }
}
