package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AgencyDao {

    // Users
    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Employees
    @Query("SELECT * FROM employees ORDER BY fullName ASC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: String): EmployeeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<EmployeeEntity>)

    @Delete
    suspend fun deleteEmployee(employee: EmployeeEntity)

    // Clients
    @Query("SELECT * FROM clients ORDER BY createdAt DESC")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: String): ClientEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    @Delete
    suspend fun deleteClient(client: ClientEntity)

    // Projects
    @Query("SELECT * FROM projects ORDER BY deadline ASC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE projectId = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE clientId = :clientId")
    fun getProjectsForClient(clientId: String): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    fun getTasksForProject(projectId: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // Payments
    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE clientId = :clientId")
    fun getPaymentsForClient(clientId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    // Calendar Events
    @Query("SELECT * FROM calendar_events ORDER BY startMillis ASC")
    fun getAllCalendarEvents(): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEvent(event: CalendarEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEvents(events: List<CalendarEventEntity>)

    @Delete
    suspend fun deleteCalendarEvent(event: CalendarEventEntity)

    // Reminders
    @Query("SELECT * FROM reminders ORDER BY dateMillis ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET `read` = 1")
    suspend fun markAllNotificationsRead()

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    // Communication Logs
    @Query("SELECT * FROM communication_logs WHERE clientId = :clientId ORDER BY dateMillis DESC")
    fun getCommunicationLogsForClient(clientId: String): Flow<List<CommunicationLogEntity>>

    @Query("SELECT * FROM communication_logs ORDER BY dateMillis DESC")
    fun getAllCommunicationLogs(): Flow<List<CommunicationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunicationLog(log: CommunicationLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunicationLogs(logs: List<CommunicationLogEntity>)

    // Activity Logs
    @Query("SELECT * FROM activity_logs ORDER BY createdAt DESC LIMIT 100")
    fun getActivityLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLogs(logs: List<ActivityLogEntity>)

    // Project Files
    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY uploadedAt DESC")
    fun getFilesForProject(projectId: String): Flow<List<ProjectFileEntity>>

    @Query("SELECT * FROM project_files WHERE clientId = :clientId ORDER BY uploadedAt DESC")
    fun getFilesForClient(clientId: String): Flow<List<ProjectFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectFile(file: ProjectFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectFiles(files: List<ProjectFileEntity>)

    @Delete
    suspend fun deleteProjectFile(file: ProjectFileEntity)

    // Settings
    @Query("SELECT * FROM settings WHERE id = 1")
    fun getSettings(): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)

    // Clear all for demo reset
    @Query("DELETE FROM clients")
    suspend fun clearClients()

    @Query("DELETE FROM employees")
    suspend fun clearEmployees()

    @Query("DELETE FROM projects")
    suspend fun clearProjects()

    @Query("DELETE FROM tasks")
    suspend fun clearTasks()

    @Query("DELETE FROM payments")
    suspend fun clearPayments()

    @Query("DELETE FROM calendar_events")
    suspend fun clearCalendarEvents()

    @Query("DELETE FROM reminders")
    suspend fun clearReminders()

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications()

    @Query("DELETE FROM communication_logs")
    suspend fun clearCommunicationLogs()

    @Query("DELETE FROM activity_logs")
    suspend fun clearActivityLogs()

    @Query("DELETE FROM project_files")
    suspend fun clearProjectFiles()
}
