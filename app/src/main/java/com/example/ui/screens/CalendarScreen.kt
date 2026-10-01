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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    state: AgencyUiState,
    viewModel: AgencyViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedEventType by remember { mutableStateOf<EventType?>(null) }
    var showAddEventDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("EEE, MMM dd • HH:mm", Locale.getDefault()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddEventDialog = true
                    else showAddReminderDialog = true
                },
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_calendar_fab")
            ) {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Default.Event else Icons.Default.AddAlarm,
                    contentDescription = "Add"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Events & Deadlines (${state.calendarEvents.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Reminders (${state.reminders.size})") }
                )
            }

            if (selectedTab == 0) {
                // Event Type Filters
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedEventType == null,
                        onClick = { selectedEventType = null },
                        label = { Text("All Events") }
                    )
                    EventType.values().forEach { type ->
                        FilterChip(
                            selected = selectedEventType == type,
                            onClick = { selectedEventType = if (selectedEventType == type) null else type },
                            label = { Text(type.label) }
                        )
                    }
                }

                val events = state.calendarEvents.filter {
                    selectedEventType == null || it.type == selectedEventType
                }

                if (events.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp), contentAlignment = Alignment.Center) {
                        Text("No upcoming events scheduled.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                    ) {
                        items(events, key = { it.id }) { event ->
                            val client = state.clients.find { it.id == event.linkedClientId }

                            Card(
                                modifier = Modifier.fillMaxWidth().testTag("event_card_${event.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val (color, icon) = when (event.type) {
                                        EventType.PROJECT_DEADLINE -> Pair(RoseError, Icons.Default.Flag)
                                        EventType.TASK_DEADLINE -> Pair(AmberWarning, Icons.Default.Checklist)
                                        EventType.MEETING -> Pair(IndigoPrimary, Icons.Default.Groups)
                                        EventType.CLIENT_CALL -> Pair(CyanAccent, Icons.Default.PhoneInTalk)
                                        EventType.DELIVERY -> Pair(EmeraldSuccess, Icons.Default.CheckCircle)
                                        EventType.REMINDER -> Pair(PurpleAccent, Icons.Default.Notifications)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(event.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dateFormat.format(Date(event.startMillis)),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (client != null) {
                                            Text(
                                                text = "Client: ${client.company}",
                                                fontSize = 11.sp,
                                                color = CyanAccent
                                            )
                                        }
                                    }

                                    IconButton(onClick = { viewModel.deleteCalendarEvent(event) }) {
                                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Reminders Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(state.reminders, key = { it.id }) { reminder ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("reminder_card_${reminder.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = reminder.completed,
                                    onCheckedChange = { viewModel.toggleReminder(reminder) },
                                    colors = CheckboxDefaults.colors(checkedColor = EmeraldSuccess)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = reminder.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = if (reminder.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (reminder.description.isNotBlank()) {
                                        Text(
                                            text = reminder.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Time: ${reminder.timeString}",
                                        fontSize = 10.sp,
                                        color = IndigoLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddEventDialog) {
        AddCalendarEventDialog(
            clients = state.clients,
            projects = state.projects,
            onDismiss = { showAddEventDialog = false },
            onSave = { newEvent ->
                viewModel.saveCalendarEvent(newEvent)
                showAddEventDialog = false
            }
        )
    }

    if (showAddReminderDialog) {
        AddReminderDialog(
            onDismiss = { showAddReminderDialog = false },
            onSave = { newRem ->
                viewModel.saveReminder(newRem)
                showAddReminderDialog = false
            }
        )
    }
}

@Composable
fun AddCalendarEventDialog(
    clients: List<Client>,
    projects: List<Project>,
    onDismiss: () -> Unit,
    onSave: (CalendarEvent) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(EventType.MEETING) }
    var selectedClientId by remember { mutableStateOf(clients.firstOrNull()?.id ?: "") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Event", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Event Title *") },
                        modifier = Modifier.fillMaxWidth().testTag("event_title_input"),
                        singleLine = true
                    )
                }

                item {
                    Text("Event Type:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        EventType.values().forEach { t ->
                            FilterChip(
                                selected = selectedType == t,
                                onClick = { selectedType = t },
                                label = { Text(t.label) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Agenda / Meeting Link") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val event = CalendarEvent(
                            id = "ev_${System.currentTimeMillis()}",
                            title = title,
                            type = selectedType,
                            startMillis = System.currentTimeMillis() + 86400000L,
                            endMillis = System.currentTimeMillis() + 86400000L + 3600000L,
                            linkedClientId = selectedClientId,
                            notes = notes
                        )
                        onSave(event)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Schedule Event")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var timeStr by remember { mutableStateOf("11:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Agency Reminder", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reminder Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details / Note") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                OutlinedTextField(
                    value = timeStr,
                    onValueChange = { timeStr = it },
                    label = { Text("Time (e.g. 11:00)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val rem = Reminder(
                            id = "rem_${System.currentTimeMillis()}",
                            title = title,
                            description = description,
                            dateMillis = System.currentTimeMillis(),
                            timeString = timeStr,
                            userId = "emp_1",
                            completed = false
                        )
                        onSave(rem)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Save Reminder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
