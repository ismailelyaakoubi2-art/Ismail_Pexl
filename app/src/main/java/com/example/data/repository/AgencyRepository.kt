package com.example.data.repository

import android.content.Context
import com.example.data.DemoData
import com.example.data.firebase.FirebaseManager
import com.example.data.local.*
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AgencyRepository(
    private val context: Context,
    private val dao: AgencyDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    // Current active user profile
    private val _currentUser = MutableStateFlow(
        UserProfile(
            uid = "emp_1",
            fullName = "Sarah Jenkins",
            email = "sarah.j@agency.com",
            phone = "+1 (555) 234-5678",
            role = UserRole.ADMIN,
            position = UserPosition.MANAGER,
            status = UserStatus.ACTIVE
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Flows from Room
    val employees: Flow<List<Employee>> = dao.getAllEmployees().map { list -> list.map { it.toModel() } }
    val clients: Flow<List<Client>> = dao.getAllClients().map { list -> list.map { it.toModel() } }
    val projects: Flow<List<Project>> = dao.getAllProjects().map { list -> list.map { it.toModel() } }
    val tasks: Flow<List<Task>> = dao.getAllTasks().map { list -> list.map { it.toModel() } }
    val payments: Flow<List<Payment>> = dao.getAllPayments().map { list -> list.map { it.toModel() } }
    val calendarEvents: Flow<List<CalendarEvent>> = dao.getAllCalendarEvents().map { list -> list.map { it.toModel() } }
    val reminders: Flow<List<Reminder>> = dao.getAllReminders().map { list -> list.map { it.toModel() } }
    val notifications: Flow<List<NotificationItem>> = dao.getAllNotifications().map { list -> list.map { it.toModel() } }
    val communicationLogs: Flow<List<CommunicationLog>> = dao.getAllCommunicationLogs().map { list -> list.map { it.toModel() } }
    val activityLogs: Flow<List<ActivityLog>> = dao.getActivityLogs().map { list -> list.map { it.toModel() } }
    val settings: Flow<AgencySettings> = dao.getSettings().map { it?.toModel() ?: AgencySettings() }

    init {
        scope.launch {
            checkAndSeedDemoData()
        }
    }

    fun switchUserRole(role: UserRole) {
        val user = when (role) {
            UserRole.ADMIN -> UserProfile(
                uid = "emp_1",
                fullName = "Sarah Jenkins",
                email = "sarah.j@agency.com",
                role = UserRole.ADMIN,
                position = UserPosition.MANAGER
            )
            UserRole.MANAGER -> UserProfile(
                uid = "emp_2",
                fullName = "Alex Rivera",
                email = "alex.r@agency.com",
                role = UserRole.MANAGER,
                position = UserPosition.DEVELOPER
            )
            UserRole.EMPLOYEE -> UserProfile(
                uid = "emp_4",
                fullName = "Marcus Chen",
                email = "marcus.c@agency.com",
                role = UserRole.EMPLOYEE,
                position = UserPosition.VIDEO_EDITOR
            )
        }
        _currentUser.value = user
    }

    suspend fun checkAndSeedDemoData() {
        val currentClients = dao.getAllClients().first()
        if (currentClients.isEmpty()) {
            resetDemoData()
        }
    }

    suspend fun resetDemoData() {
        dao.clearClients()
        dao.clearEmployees()
        dao.clearProjects()
        dao.clearTasks()
        dao.clearPayments()
        dao.clearCalendarEvents()
        dao.clearReminders()
        dao.clearNotifications()
        dao.clearCommunicationLogs()
        dao.clearActivityLogs()
        dao.clearProjectFiles()

        dao.insertEmployees(DemoData.employees.map { EmployeeEntity.fromModel(it) })
        dao.insertClients(DemoData.clients.map { ClientEntity.fromModel(it) })
        dao.insertProjects(DemoData.projects.map { ProjectEntity.fromModel(it) })
        dao.insertTasks(DemoData.tasks.map { TaskEntity.fromModel(it) })
        dao.insertPayments(DemoData.payments.map { PaymentEntity.fromModel(it) })
        dao.insertCalendarEvents(DemoData.calendarEvents.map { CalendarEventEntity.fromModel(it) })
        dao.insertReminders(DemoData.reminders.map { ReminderEntity.fromModel(it) })
        dao.insertNotifications(DemoData.notifications.map { NotificationEntity.fromModel(it) })
        dao.insertCommunicationLogs(DemoData.communicationLogs.map { CommunicationLogEntity.fromModel(it) })
        dao.insertActivityLogs(DemoData.activityLogs.map { ActivityLogEntity.fromModel(it) })
        dao.insertProjectFiles(DemoData.projectFiles.map { ProjectFileEntity.fromModel(it) })
        dao.insertSettings(SettingsEntity.fromModel(AgencySettings()))
    }

    // CRUD for Client
    suspend fun saveClient(client: Client) {
        dao.insertClient(ClientEntity.fromModel(client))
        logActivity("Client", client.id, "Saved client: ${client.name} (${client.company})")
        FirebaseManager.syncDocToFirestore(
            context,
            "clients",
            client.id,
            mapOf(
                "name" to client.name,
                "company" to client.company,
                "email" to client.email,
                "phone" to client.phone,
                "status" to client.status.name,
                "source" to client.source.name,
                "assignedEmployeeId" to client.assignedEmployeeId,
                "updatedAt" to System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteClient(client: Client) {
        dao.deleteClient(ClientEntity.fromModel(client))
        logActivity("Client", client.id, "Deleted client: ${client.name}")
        FirebaseManager.deleteDocFromFirestore(context, "clients", client.id)
    }

    // CRUD for Project
    suspend fun saveProject(project: Project) {
        dao.insertProject(ProjectEntity.fromModel(project))
        logActivity("Project", project.projectId, "Saved project: ${project.name} (${project.status.label})")
        FirebaseManager.syncDocToFirestore(
            context,
            "projects",
            project.projectId,
            mapOf(
                "name" to project.name,
                "clientId" to project.clientId,
                "assignedEmployeeIds" to project.assignedEmployeeIds,
                "status" to project.status.name,
                "priority" to project.priority.name,
                "progress" to project.progress,
                "deadline" to project.deadline,
                "budget" to project.budget,
                "updatedAt" to System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteProject(project: Project) {
        dao.deleteProject(ProjectEntity.fromModel(project))
        logActivity("Project", project.projectId, "Deleted project: ${project.name}")
        FirebaseManager.deleteDocFromFirestore(context, "projects", project.projectId)
    }

    // CRUD for Task
    suspend fun saveTask(task: Task) {
        dao.insertTask(TaskEntity.fromModel(task))
        logActivity("Task", task.id, "Saved task: ${task.title} -> ${task.status.label}")
        FirebaseManager.syncDocToFirestore(
            context,
            "tasks",
            task.id,
            mapOf(
                "title" to task.title,
                "description" to task.description,
                "projectId" to task.projectId,
                "clientId" to task.clientId,
                "assignedEmployeeId" to task.assignedEmployeeId,
                "status" to task.status.name,
                "priority" to task.priority.name,
                "dueDate" to task.dueDate
            )
        )
    }

    suspend fun deleteTask(task: Task) {
        dao.deleteTask(TaskEntity.fromModel(task))
        logActivity("Task", task.id, "Deleted task: ${task.title}")
        FirebaseManager.deleteDocFromFirestore(context, "tasks", task.id)
    }

    // CRUD for Payment
    suspend fun savePayment(payment: Payment) {
        dao.insertPayment(PaymentEntity.fromModel(payment))
        logActivity("Payment", payment.id, "Recorded payment of ${payment.amount} ${payment.currency} (${payment.status.label})")
        FirebaseManager.syncDocToFirestore(
            context,
            "payments",
            payment.id,
            mapOf(
                "clientId" to payment.clientId,
                "projectId" to payment.projectId,
                "amount" to payment.amount,
                "currency" to payment.currency,
                "status" to payment.status.name,
                "paymentDate" to payment.paymentDate
            )
        )
    }

    // CRUD for Employee
    suspend fun saveEmployee(employee: Employee) {
        dao.insertEmployee(EmployeeEntity.fromModel(employee))
        logActivity("Employee", employee.id, "Saved team member: ${employee.fullName}")
    }

    suspend fun deleteEmployee(employee: Employee) {
        dao.deleteEmployee(EmployeeEntity.fromModel(employee))
        logActivity("Employee", employee.id, "Removed team member: ${employee.fullName}")
    }

    // Calendar & Reminders
    suspend fun saveCalendarEvent(event: CalendarEvent) {
        dao.insertCalendarEvent(CalendarEventEntity.fromModel(event))
        logActivity("Calendar", event.id, "Scheduled event: ${event.title}")
    }

    suspend fun deleteCalendarEvent(event: CalendarEvent) {
        dao.deleteCalendarEvent(CalendarEventEntity.fromModel(event))
    }

    suspend fun saveReminder(reminder: Reminder) {
        dao.insertReminder(ReminderEntity.fromModel(reminder))
        logActivity("Reminder", reminder.id, "Saved reminder: ${reminder.title}")
    }

    suspend fun toggleReminder(reminder: Reminder) {
        val updated = reminder.copy(completed = !reminder.completed)
        dao.insertReminder(ReminderEntity.fromModel(updated))
    }

    // Communication Log
    suspend fun addCommunicationLog(clientId: String, type: CommunicationType, note: String) {
        val log = CommunicationLog(
            id = "com_${System.currentTimeMillis()}",
            clientId = clientId,
            dateMillis = System.currentTimeMillis(),
            type = type,
            employeeId = _currentUser.value.uid,
            note = note
        )
        dao.insertCommunicationLog(CommunicationLogEntity.fromModel(log))
        // Update client last contact timestamp
        val client = dao.getClientById(clientId)
        if (client != null) {
            dao.insertClient(client.copy(lastContactAt = System.currentTimeMillis()))
        }
        logActivity("Communication", log.id, "Logged ${type.label} communication with client")
    }

    fun getCommunicationLogsForClient(clientId: String): Flow<List<CommunicationLog>> {
        return dao.getCommunicationLogsForClient(clientId).map { list -> list.map { it.toModel() } }
    }

    // Files
    fun getFilesForProject(projectId: String): Flow<List<ProjectFile>> {
        return dao.getFilesForProject(projectId).map { list -> list.map { it.toModel() } }
    }

    fun getFilesForClient(clientId: String): Flow<List<ProjectFile>> {
        return dao.getFilesForClient(clientId).map { list -> list.map { it.toModel() } }
    }

    suspend fun saveProjectFile(file: ProjectFile) {
        dao.insertProjectFile(ProjectFileEntity.fromModel(file))
        logActivity("File", file.id, "Uploaded file: ${file.fileName} (${file.category.label})")
    }

    suspend fun deleteProjectFile(file: ProjectFile) {
        dao.deleteProjectFile(ProjectFileEntity.fromModel(file))
        logActivity("File", file.id, "Deleted file: ${file.fileName}")
    }

    // Settings
    suspend fun updateSettings(settings: AgencySettings) {
        dao.insertSettings(SettingsEntity.fromModel(settings))
        logActivity("Settings", "1", "Updated agency settings")
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    private suspend fun logActivity(entityType: String, entityId: String, description: String) {
        val user = _currentUser.value
        val log = ActivityLog(
            id = "act_${System.currentTimeMillis()}_${(100..999).random()}",
            actorUserId = user.uid,
            actorName = user.fullName,
            action = "ACTION",
            entityType = entityType,
            entityId = entityId,
            description = description,
            createdAt = System.currentTimeMillis()
        )
        dao.insertActivityLog(ActivityLogEntity.fromModel(log))
    }

    // Client Health Calculator
    fun calculateClientHealth(
        client: Client,
        clientProjects: List<Project>,
        clientPayments: List<Payment>
    ): ClientHealthResult {
        val reasons = mutableListOf<String>()
        val now = System.currentTimeMillis()
        val daysSinceLastContact = TimeUnit.MILLISECONDS.toDays(now - client.lastContactAt)

        val overdueProjects = clientProjects.filter {
            it.status != ProjectStatus.COMPLETED &&
            it.status != ProjectStatus.DELIVERED &&
            it.status != ProjectStatus.CANCELLED &&
            it.deadline < now
        }
        val delayedProjects = clientProjects.filter { it.status == ProjectStatus.DELAYED }
        val overduePayments = clientPayments.filter { it.status == PaymentStatus.OVERDUE }

        if (daysSinceLastContact > 14) {
            reasons.add("No contact in $daysSinceLastContact days (> 14 days)")
        } else if (daysSinceLastContact > 10) {
            reasons.add("Last contact was $daysSinceLastContact days ago")
        }

        if (overdueProjects.isNotEmpty()) {
            reasons.add("${overdueProjects.size} project(s) past deadline")
        }

        if (delayedProjects.isNotEmpty()) {
            reasons.add("${delayedProjects.size} project(s) marked as Delayed")
        }

        if (overduePayments.isNotEmpty()) {
            val totalOverdue = overduePayments.sumOf { it.amount }
            reasons.add("Overdue invoice of $${String.format(Locale.US, "%.0f", totalOverdue)}")
        }

        val health = when {
            reasons.size >= 2 || overdueProjects.size >= 2 || delayedProjects.isNotEmpty() -> ClientHealth.AT_RISK
            reasons.isNotEmpty() -> ClientHealth.ATTENTION
            else -> {
                reasons.add("Regular communication and projects on track")
                ClientHealth.GOOD
            }
        }

        return ClientHealthResult(health, reasons)
    }

    // Deadline Formatting
    fun formatDeadlineInfo(deadlineMillis: Long, status: ProjectStatus): DeadlineInfo {
        if (status == ProjectStatus.COMPLETED || status == ProjectStatus.DELIVERED) {
            return DeadlineInfo("Delivered", DeadlineUrgency.DELIVERED)
        }
        val now = System.currentTimeMillis()
        val diffMillis = deadlineMillis - now
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)
        val diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis)

        return when {
            diffMillis < 0 -> {
                val overdueDays = TimeUnit.MILLISECONDS.toDays(-diffMillis)
                val overdueHours = TimeUnit.MILLISECONDS.toHours(-diffMillis)
                val text = if (overdueDays > 0) "Overdue by $overdueDays day${if (overdueDays > 1) "s" else ""}"
                           else "Overdue by $overdueHours hour${if (overdueHours > 1) "s" else ""}"
                DeadlineInfo(text, DeadlineUrgency.OVERDUE)
            }
            diffDays == 0L && diffHours <= 24 -> {
                val text = if (diffHours <= 1) "Due in under an hour" else "Due in $diffHours hours"
                DeadlineInfo(text, DeadlineUrgency.DUE_SOON)
            }
            diffDays == 1L -> DeadlineInfo("Due tomorrow", DeadlineUrgency.DUE_SOON)
            diffDays <= 3L -> DeadlineInfo("$diffDays days left", DeadlineUrgency.DUE_SOON)
            else -> DeadlineInfo("$diffDays days left", DeadlineUrgency.NORMAL)
        }
    }
}

enum class DeadlineUrgency {
    NORMAL,
    DUE_SOON,
    OVERDUE,
    DELIVERED
}

data class DeadlineInfo(
    val displayText: String,
    val urgency: DeadlineUrgency
)
