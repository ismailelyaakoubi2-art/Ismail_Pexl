package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentsScreen(
    state: AgencyUiState,
    viewModel: AgencyViewModel
) {
    val isEmployee = state.currentUser.role == UserRole.EMPLOYEE

    if (isEmployee) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = RoseError,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Access Restricted",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Payment details and financial transactions are restricted to Agency Admins and Managers. Please contact your supervisor for access.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
        return
    }

    var selectedStatusFilter by remember { mutableStateOf<PaymentStatus?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredPayments = remember(state.payments, selectedStatusFilter) {
        state.payments.filter { payment ->
            selectedStatusFilter == null || payment.status == selectedStatusFilter
        }
    }

    val totalPaid = state.payments.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount }
    val totalPending = state.payments.filter { it.status == PaymentStatus.PENDING }.sumOf { it.amount }
    val totalOverdue = state.payments.filter { it.status == PaymentStatus.OVERDUE }.sumOf { it.amount }
    val totalRevenue = totalPaid + totalPending

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_payment_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Record Payment")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Metrics Summary Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Collected",
                    value = "$${String.format(Locale.US, "%,.0f", totalPaid)}",
                    subtitle = "Paid In Full",
                    icon = Icons.Default.CheckCircle,
                    accentColor = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pending",
                    value = "$${String.format(Locale.US, "%,.0f", totalPending)}",
                    subtitle = "Awaiting Release",
                    icon = Icons.Default.Schedule,
                    accentColor = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filters Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text("All Payments (${state.payments.size})") }
                )
                PaymentStatus.values().forEach { st ->
                    FilterChip(
                        selected = selectedStatusFilter == st,
                        onClick = { selectedStatusFilter = if (selectedStatusFilter == st) null else st },
                        label = { Text(st.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredPayments, key = { it.id }) { payment ->
                    val client = state.clients.find { it.id == payment.clientId }
                    val project = state.projects.find { it.projectId == payment.projectId }

                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("payment_card_${payment.id}"),
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
                                Column {
                                    Text(
                                        text = "$${String.format(Locale.US, "%,.2f", payment.amount)} ${payment.currency}",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = client?.company ?: "Client #${payment.clientId}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CyanAccent
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

                            if (project != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Project: ${project.name}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Method: ${payment.paymentMethod.label}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = dateFormat.format(Date(payment.paymentDate)),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (payment.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = payment.notes,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        PaymentEditorDialog(
            clients = state.clients,
            projects = state.projects,
            onDismiss = { showAddDialog = false },
            onSave = { newPayment ->
                viewModel.savePayment(newPayment)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PaymentEditorDialog(
    clients: List<Client>,
    projects: List<Project>,
    onDismiss: () -> Unit,
    onSave: (Payment) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var selectedClientId by remember { mutableStateOf(clients.firstOrNull()?.id ?: "") }
    var selectedProjectId by remember { mutableStateOf(projects.firstOrNull()?.projectId ?: "") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BANK_TRANSFER) }
    var selectedStatus by remember { mutableStateOf(PaymentStatus.PAID) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record New Payment", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount (USD) *") },
                        modifier = Modifier.fillMaxWidth().testTag("payment_amount_input"),
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
                    Text("Payment Method:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentMethod.values().forEach { m ->
                            FilterChip(
                                selected = selectedMethod == m,
                                onClick = { selectedMethod = m },
                                label = { Text(m.label) }
                            )
                        }
                    }
                }

                item {
                    Text("Status:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PaymentStatus.values().forEach { st ->
                            FilterChip(
                                selected = selectedStatus == st,
                                onClick = { selectedStatus = st },
                                label = { Text(st.label) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Invoice / Transaction Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        val payment = Payment(
                            id = "pay_${System.currentTimeMillis()}",
                            clientId = selectedClientId,
                            projectId = selectedProjectId,
                            amount = amount,
                            currency = "USD",
                            paymentMethod = selectedMethod,
                            paymentDate = System.currentTimeMillis(),
                            status = selectedStatus,
                            notes = notes,
                            createdAt = System.currentTimeMillis(),
                            createdBy = "Agency Admin"
                        )
                        onSave(payment)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("save_payment_button")
            ) {
                Text("Record Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
