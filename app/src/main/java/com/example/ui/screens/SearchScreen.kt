package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgencyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: AgencyUiState,
    onBack: () -> Unit,
    onSelectClient: (String) -> Unit,
    onSelectProject: (String) -> Unit,
    onSelectTask: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val matchedClients = remember(state.clients, query) {
        if (query.isBlank()) emptyList()
        else state.clients.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.company.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true)
        }
    }

    val matchedProjects = remember(state.projects, query) {
        if (query.isBlank()) emptyList()
        else state.projects.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.notes.contains(query, ignoreCase = true)
        }
    }

    val matchedTasks = remember(state.tasks, query) {
        if (query.isBlank()) emptyList()
        else state.tasks.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
        }
    }

    val matchedEmployees = remember(state.employees, query) {
        if (query.isBlank()) emptyList()
        else state.employees.filter {
            it.fullName.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true) ||
            it.position.title.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search everything...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp)
                            .testTag("global_search_field"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
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
            if (query.isBlank()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Search across clients, projects, tasks, and employees.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Clients Results
                if (matchedClients.isNotEmpty()) {
                    item {
                        Text(
                            text = "Clients (${matchedClients.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = CyanAccent
                        )
                    }
                    items(matchedClients) { c ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectClient(c.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(c.company, fontWeight = FontWeight.Bold)
                                    Text(c.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                StatusBadge(text = c.status.label, color = EmeraldSuccess)
                            }
                        }
                    }
                }

                // Projects Results
                if (matchedProjects.isNotEmpty()) {
                    item {
                        Text(
                            text = "Projects (${matchedProjects.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = IndigoLight
                        )
                    }
                    items(matchedProjects) { p ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectProject(p.projectId) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(p.name, fontWeight = FontWeight.Bold)
                                    Text(p.type.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                StatusBadge(text = p.status.label, color = IndigoPrimary)
                            }
                        }
                    }
                }

                // Tasks Results
                if (matchedTasks.isNotEmpty()) {
                    item {
                        Text(
                            text = "Tasks (${matchedTasks.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = PurpleAccent
                        )
                    }
                    items(matchedTasks) { t ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectTask(t.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(t.title, fontWeight = FontWeight.Bold)
                                    Text(t.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                }
                                StatusBadge(text = t.status.label, color = CyanAccent)
                            }
                        }
                    }
                }

                // Employees Results
                if (matchedEmployees.isNotEmpty()) {
                    item {
                        Text(
                            text = "Team Members (${matchedEmployees.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldSuccess
                        )
                    }
                    items(matchedEmployees) { emp ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(emp.fullName, fontWeight = FontWeight.Bold)
                                    Text(emp.position.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                StatusBadge(text = emp.status.label, color = IndigoLight)
                            }
                        }
                    }
                }

                if (matchedClients.isEmpty() && matchedProjects.isEmpty() && matchedTasks.isEmpty() && matchedEmployees.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No results matching \"$query\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
