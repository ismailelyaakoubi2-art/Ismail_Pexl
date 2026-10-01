package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirebaseManager
import com.example.data.local.AppDatabase
import com.example.data.repository.*
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AgencyUiState(
    val currentUser: UserProfile = UserProfile(),
    val clients: List<Client> = emptyList(),
    val employees: List<Employee> = emptyList(),
    val projects: List<Project> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val calendarEvents: List<CalendarEvent> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val communicationLogs: List<CommunicationLog> = emptyList(),
    val activityLogs: List<ActivityLog> = emptyList(),
    val settings: AgencySettings = AgencySettings(),
    val searchQuery: String = "",
    val activeSection: String = "dashboard",
    val isFirebaseReady: Boolean = false,
    val toastMessage: String? = null
)

class AgencyViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = AgencyRepository(application, database.agencyDao())

    private val _uiState = MutableStateFlow(AgencyUiState())
    val uiState: StateFlow<AgencyUiState> = _uiState.asStateFlow()

    init {
        val isFirebaseReady = FirebaseManager.isFirebaseAvailable(application)
        _uiState.update { it.copy(isFirebaseReady = isFirebaseReady) }

        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
        viewModelScope.launch {
            repository.clients.collect { list ->
                _uiState.update { it.copy(clients = list) }
            }
        }
        viewModelScope.launch {
            repository.employees.collect { list ->
                _uiState.update { it.copy(employees = list) }
            }
        }
        viewModelScope.launch {
            repository.projects.collect { list ->
                _uiState.update { it.copy(projects = list) }
            }
        }
        viewModelScope.launch {
            repository.tasks.collect { list ->
                _uiState.update { it.copy(tasks = list) }
            }
        }
        viewModelScope.launch {
            repository.payments.collect { list ->
                _uiState.update { it.copy(payments = list) }
            }
        }
        viewModelScope.launch {
            repository.calendarEvents.collect { list ->
                _uiState.update { it.copy(calendarEvents = list) }
            }
        }
        viewModelScope.launch {
            repository.reminders.collect { list ->
                _uiState.update { it.copy(reminders = list) }
            }
        }
        viewModelScope.launch {
            repository.notifications.collect { list ->
                _uiState.update { it.copy(notifications = list) }
            }
        }
        viewModelScope.launch {
            repository.communicationLogs.collect { list ->
                _uiState.update { it.copy(communicationLogs = list) }
            }
        }
        viewModelScope.launch {
            repository.activityLogs.collect { list ->
                _uiState.update { it.copy(activityLogs = list) }
            }
        }
        viewModelScope.launch {
            repository.settings.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun setSection(section: String) {
        _uiState.update { it.copy(activeSection = section) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun switchRole(role: UserRole) {
        repository.switchUserRole(role)
        showToast("Switched active view to ${role.name}")
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun saveClient(client: Client) {
        viewModelScope.launch {
            repository.saveClient(client)
            showToast("Client '${client.name}' saved")
        }
    }

    fun deleteClient(client: Client) {
        viewModelScope.launch {
            repository.deleteClient(client)
            showToast("Client '${client.name}' deleted")
        }
    }

    fun saveProject(project: Project) {
        viewModelScope.launch {
            repository.saveProject(project)
            showToast("Project '${project.name}' saved")
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
            showToast("Project '${project.name}' deleted")
        }
    }

    fun deliverProject(project: Project, notes: String) {
        viewModelScope.launch {
            val user = uiState.value.currentUser
            val updated = project.copy(
                status = ProjectStatus.DELIVERED,
                progress = 100,
                deliveredAt = System.currentTimeMillis(),
                deliveredBy = user.fullName,
                deliveryNotes = notes,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveProject(updated)
            showToast("Project marked as Delivered!")
        }
    }

    fun saveTask(task: Task) {
        viewModelScope.launch {
            repository.saveTask(task)
            showToast("Task saved")
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            showToast("Task removed")
        }
    }

    fun moveTaskStatus(task: Task, nextStatus: TaskStatus) {
        viewModelScope.launch {
            val updated = task.copy(
                status = nextStatus,
                completedAt = if (nextStatus == TaskStatus.DONE) System.currentTimeMillis() else null
            )
            repository.saveTask(updated)
            showToast("Moved to ${nextStatus.label}")
        }
    }

    fun savePayment(payment: Payment) {
        viewModelScope.launch {
            repository.savePayment(payment)
            showToast("Payment recorded")
        }
    }

    fun saveEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.saveEmployee(employee)
            showToast("Team member updated")
        }
    }

    fun deleteEmployee(employee: Employee) {
        viewModelScope.launch {
            repository.deleteEmployee(employee)
            showToast("Team member removed")
        }
    }

    fun saveCalendarEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.saveCalendarEvent(event)
            showToast("Event saved")
        }
    }

    fun deleteCalendarEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.deleteCalendarEvent(event)
            showToast("Event removed")
        }
    }

    fun saveReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.saveReminder(reminder)
            showToast("Reminder added")
        }
    }

    fun toggleReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.toggleReminder(reminder)
        }
    }

    fun addCommunicationLog(clientId: String, type: CommunicationType, note: String) {
        viewModelScope.launch {
            repository.addCommunicationLog(clientId, type, note)
            showToast("Communication logged")
        }
    }

    fun saveProjectFile(file: ProjectFile) {
        viewModelScope.launch {
            repository.saveProjectFile(file)
            showToast("File uploaded")
        }
    }

    fun deleteProjectFile(file: ProjectFile) {
        viewModelScope.launch {
            repository.deleteProjectFile(file)
            showToast("File deleted")
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            showToast("All notifications marked as read")
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
            showToast("Demo data reloaded successfully")
        }
    }

    fun updateSettings(settings: AgencySettings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
            showToast("Settings updated")
        }
    }
}
