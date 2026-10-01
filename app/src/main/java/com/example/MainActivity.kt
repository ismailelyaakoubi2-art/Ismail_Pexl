package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AgencyViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: AgencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current

            LaunchedEffect(uiState.toastMessage) {
                uiState.toastMessage?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.clearToast()
                }
            }

            OmniAgencyTheme(
                darkTheme = uiState.settings.isDarkMode,
                language = uiState.settings.language
            ) {
                AgencyAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AgencyAppContent(viewModel: AgencyViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var activeScreen by remember { mutableStateOf("dashboard") }
    var selectedClientId by remember { mutableStateOf<String?>(null) }
    var selectedProjectId by remember { mutableStateOf<String?>(null) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var showQuickActionDialog by remember { mutableStateOf(false) }

    var showAddClientModal by remember { mutableStateOf(false) }
    var showAddProjectModal by remember { mutableStateOf(false) }
    var showAddTaskModal by remember { mutableStateOf(false) }
    var showAddPaymentModal by remember { mutableStateOf(false) }
    var showAddReminderModal by remember { mutableStateOf(false) }

    val unreadCount = state.notifications.count { !it.read }

    BackHandler(enabled = drawerState.isOpen || selectedClientId != null || selectedProjectId != null || activeScreen == "search" || activeScreen == "notifications") {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }
            selectedClientId != null -> selectedClientId = null
            selectedProjectId != null -> selectedProjectId = null
            activeScreen == "search" -> activeScreen = "dashboard"
            activeScreen == "notifications" -> activeScreen = "dashboard"
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        val isWideScreen = maxWidth > 680.dp

        if (isWideScreen) {
            // Permanent Navigation Drawer for larger screens (Tablets, Desktop, Foldables)
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(280.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surface
                    ) {
                        AgencyDrawerSheetContent(
                            activeScreen = activeScreen,
                            onSelectScreen = { screen ->
                                activeScreen = screen
                                selectedClientId = null
                                selectedProjectId = null
                            },
                            currentUser = state.currentUser,
                            agencyName = state.settings.agencyName,
                            unreadNotifications = unreadCount
                        )
                    }
                }
            ) {
                AgencyMainScaffold(
                    activeScreen = activeScreen,
                    isWideScreen = true,
                    state = state,
                    unreadCount = unreadCount,
                    selectedClientId = selectedClientId,
                    selectedProjectId = selectedProjectId,
                    onOpenDrawer = null, // No hamburger needed with permanent drawer
                    onNavigate = { activeScreen = it },
                    onSelectClient = { selectedClientId = it },
                    onSelectProject = { selectedProjectId = it },
                    onClearSelectedClient = { selectedClientId = null },
                    onClearSelectedProject = { selectedProjectId = null },
                    onShowRoleDialog = { showRoleDialog = true },
                    onShowQuickActionDialog = { showQuickActionDialog = true },
                    onQuickAction = { action ->
                        when (action) {
                            "client" -> showAddClientModal = true
                            "project" -> showAddProjectModal = true
                            "task" -> showAddTaskModal = true
                            "payment" -> showAddPaymentModal = true
                            "reminder" -> showAddReminderModal = true
                        }
                    },
                    viewModel = viewModel
                )
            }
        } else {
            // Modal Navigation Drawer for mobile phones
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AgencyDrawerSheetContent(
                        activeScreen = activeScreen,
                        onSelectScreen = { screen ->
                            activeScreen = screen
                            selectedClientId = null
                            selectedProjectId = null
                            scope.launch { drawerState.close() }
                        },
                        currentUser = state.currentUser,
                        agencyName = state.settings.agencyName,
                        unreadNotifications = unreadCount
                    )
                }
            ) {
                AgencyMainScaffold(
                    activeScreen = activeScreen,
                    isWideScreen = false,
                    state = state,
                    unreadCount = unreadCount,
                    selectedClientId = selectedClientId,
                    selectedProjectId = selectedProjectId,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onNavigate = { activeScreen = it },
                    onSelectClient = { selectedClientId = it },
                    onSelectProject = { selectedProjectId = it },
                    onClearSelectedClient = { selectedClientId = null },
                    onClearSelectedProject = { selectedProjectId = null },
                    onShowRoleDialog = { showRoleDialog = true },
                    onShowQuickActionDialog = { showQuickActionDialog = true },
                    onQuickAction = { action ->
                        when (action) {
                            "client" -> showAddClientModal = true
                            "project" -> showAddProjectModal = true
                            "task" -> showAddTaskModal = true
                            "payment" -> showAddPaymentModal = true
                            "reminder" -> showAddReminderModal = true
                        }
                    },
                    viewModel = viewModel
                )
            }
        }
    }

    // Role Switch Dialog
    if (showRoleDialog) {
        RoleSwitchDialog(
            currentRole = state.currentUser.role,
            onRoleSelected = { role ->
                viewModel.switchRole(role)
            },
            onDismiss = { showRoleDialog = false }
        )
    }

    // Quick Action Dialog
    if (showQuickActionDialog) {
        AlertDialog(
            onDismissRequest = { showQuickActionDialog = false },
            title = { Text("Agency Quick Actions", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            showQuickActionDialog = false
                            showAddClientModal = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Client")
                    }

                    Button(
                        onClick = {
                            showQuickActionDialog = false
                            showAddProjectModal = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Project")
                    }

                    Button(
                        onClick = {
                            showQuickActionDialog = false
                            showAddTaskModal = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                    ) {
                        Icon(Icons.Default.AddTask, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Task")
                    }

                    if (state.currentUser.role != UserRole.EMPLOYEE) {
                        Button(
                            onClick = {
                                showQuickActionDialog = false
                                showAddPaymentModal = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                        ) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("New Payment")
                        }
                    }

                    Button(
                        onClick = {
                            showQuickActionDialog = false
                            showAddReminderModal = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarning)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Reminder")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQuickActionDialog = false }) { Text("Close") }
            }
        )
    }

    // Modal dialogs
    if (showAddClientModal) {
        ClientEditorDialog(
            client = null,
            employees = state.employees,
            onDismiss = { showAddClientModal = false },
            onSave = {
                viewModel.saveClient(it)
                showAddClientModal = false
            }
        )
    }

    if (showAddProjectModal) {
        ProjectEditorDialog(
            project = null,
            clients = state.clients,
            employees = state.employees,
            onDismiss = { showAddProjectModal = false },
            onSave = {
                viewModel.saveProject(it)
                showAddProjectModal = false
            }
        )
    }

    if (showAddTaskModal) {
        TaskEditorDialog(
            projects = state.projects,
            employees = state.employees,
            onDismiss = { showAddTaskModal = false },
            onSave = {
                viewModel.saveTask(it)
                showAddTaskModal = false
            }
        )
    }

    if (showAddPaymentModal) {
        PaymentEditorDialog(
            clients = state.clients,
            projects = state.projects,
            onDismiss = { showAddPaymentModal = false },
            onSave = {
                viewModel.savePayment(it)
                showAddPaymentModal = false
            }
        )
    }

    if (showAddReminderModal) {
        AddReminderDialog(
            onDismiss = { showAddReminderModal = false },
            onSave = {
                viewModel.saveReminder(it)
                showAddReminderModal = false
            }
        )
    }
}

@Composable
private fun AgencyMainScaffold(
    activeScreen: String,
    isWideScreen: Boolean,
    state: com.example.ui.viewmodel.AgencyUiState,
    unreadCount: Int,
    selectedClientId: String?,
    selectedProjectId: String?,
    onOpenDrawer: (() -> Unit)?,
    onNavigate: (String) -> Unit,
    onSelectClient: (String) -> Unit,
    onSelectProject: (String) -> Unit,
    onClearSelectedClient: () -> Unit,
    onClearSelectedProject: () -> Unit,
    onShowRoleDialog: () -> Unit,
    onShowQuickActionDialog: () -> Unit,
    onQuickAction: (String) -> Unit,
    viewModel: AgencyViewModel
) {
    Scaffold(
        topBar = {
            if (selectedClientId == null && selectedProjectId == null && activeScreen != "search") {
                val sectionTitle = when (activeScreen) {
                    "dashboard" -> "Dashboard"
                    "clients" -> "Clients"
                    "projects" -> "Projects"
                    "tasks" -> "Tasks"
                    "calendar" -> "Calendar"
                    "payments" -> "Payments"
                    "employees" -> "Team"
                    "reports" -> "Reports"
                    "activity" -> "Activity Log"
                    "settings" -> "Settings"
                    "notifications" -> "Notifications"
                    else -> "OmniAgency"
                }

                AgencyTopBar(
                    title = sectionTitle,
                    currentUser = state.currentUser,
                    unreadNotifications = unreadCount,
                    isFirebaseReady = state.isFirebaseReady,
                    onMenuClick = onOpenDrawer,
                    onRoleClick = onShowRoleDialog,
                    onNotificationClick = { onNavigate("notifications") },
                    onSearchClick = { onNavigate("search") },
                    onQuickActionClick = onShowQuickActionDialog
                )
            }
        },
        bottomBar = {
            // BottomNavigation for Mobile Screens
            if (!isWideScreen && selectedClientId == null && selectedProjectId == null && activeScreen != "search" && activeScreen != "notifications") {
                NavigationBar(
                    modifier = Modifier.navigationBarsPadding(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val mobileItems = listOf(
                        Triple("dashboard", "Dashboard", Icons.Outlined.Dashboard),
                        Triple("clients", "Clients", Icons.Outlined.Business),
                        Triple("projects", "Projects", Icons.Outlined.Folder),
                        Triple("tasks", "Tasks", Icons.Outlined.Checklist),
                        Triple("more", "More", Icons.Outlined.Menu)
                    )

                    mobileItems.forEach { (route, label, icon) ->
                        val isSelected = if (route == "more") {
                            activeScreen in listOf("calendar", "payments", "employees", "reports", "activity", "settings")
                        } else {
                            activeScreen == route
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (route == "more") {
                                    onOpenDrawer?.invoke() ?: onNavigate("settings")
                                } else {
                                    onNavigate(route)
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_bottom_$route")
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when {
                selectedClientId != null -> {
                    ClientDetailScreen(
                        clientId = selectedClientId,
                        state = state,
                        viewModel = viewModel,
                        onBack = onClearSelectedClient,
                        onOpenProject = onSelectProject
                    )
                }
                selectedProjectId != null -> {
                    ProjectDetailScreen(
                        projectId = selectedProjectId,
                        state = state,
                        viewModel = viewModel,
                        onBack = onClearSelectedProject,
                        onOpenClient = onSelectClient
                    )
                }
                activeScreen == "search" -> {
                    SearchScreen(
                        state = state,
                        onBack = { onNavigate("dashboard") },
                        onSelectClient = onSelectClient,
                        onSelectProject = onSelectProject,
                        onSelectTask = { onNavigate("tasks") }
                    )
                }
                activeScreen == "notifications" -> {
                    NotificationsScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }
                activeScreen == "dashboard" -> {
                    DashboardScreen(
                        state = state,
                        viewModel = viewModel,
                        onNavigate = onNavigate,
                        onOpenClient = onSelectClient,
                        onOpenProject = onSelectProject,
                        onQuickAction = onQuickAction
                    )
                }
                activeScreen == "clients" -> {
                    ClientsScreen(
                        state = state,
                        viewModel = viewModel,
                        onSelectClient = onSelectClient
                    )
                }
                activeScreen == "projects" -> {
                    ProjectsScreen(
                        state = state,
                        viewModel = viewModel,
                        onSelectProject = onSelectProject
                    )
                }
                activeScreen == "tasks" -> {
                    TasksScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }
                activeScreen == "calendar" -> {
                    CalendarScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }
                activeScreen == "payments" -> {
                    PaymentsScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }
                activeScreen == "employees" -> {
                    EmployeesScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }
                activeScreen == "reports" -> {
                    ReportsScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }
                activeScreen == "activity" -> {
                    ActivityLogScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }
                activeScreen == "settings" -> {
                    SettingsScreen(
                        state = state,
                        viewModel = viewModel,
                        onToggleTheme = { isDark ->
                            viewModel.updateSettings(state.settings.copy(isDarkMode = isDark))
                        },
                        onLanguageChange = { lang ->
                            viewModel.updateSettings(state.settings.copy(language = lang))
                        }
                    )
                }
            }
        }
    }
}
