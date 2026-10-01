package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.*

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val photoURL: String,
    val role: String,
    val position: String,
    val status: String,
    val createdAt: Long,
    val lastLoginAt: Long
) {
    fun toModel(): UserProfile = UserProfile(
        uid = uid,
        fullName = fullName,
        email = email,
        phone = phone,
        photoURL = photoURL,
        role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.EMPLOYEE },
        position = try { UserPosition.valueOf(position) } catch (e: Exception) { UserPosition.DESIGNER },
        status = try { UserStatus.valueOf(status) } catch (e: Exception) { UserStatus.ACTIVE },
        createdAt = createdAt,
        lastLoginAt = lastLoginAt
    )

    companion object {
        fun fromModel(model: UserProfile): UserEntity = UserEntity(
            uid = model.uid,
            fullName = model.fullName,
            email = model.email,
            phone = model.phone,
            photoURL = model.photoURL,
            role = model.role.name,
            position = model.position.name,
            status = model.status.name,
            createdAt = model.createdAt,
            lastLoginAt = model.lastLoginAt
        )
    }
}

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String,
    val position: String,
    val status: String,
    val hourlyRate: Double,
    val avatarColor: Long,
    val createdAt: Long
) {
    fun toModel(): Employee = Employee(
        id = id,
        fullName = fullName,
        email = email,
        phone = phone,
        role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.EMPLOYEE },
        position = try { UserPosition.valueOf(position) } catch (e: Exception) { UserPosition.DESIGNER },
        status = try { UserStatus.valueOf(status) } catch (e: Exception) { UserStatus.ACTIVE },
        hourlyRate = hourlyRate,
        avatarColor = avatarColor,
        createdAt = createdAt
    )

    companion object {
        fun fromModel(model: Employee): EmployeeEntity = EmployeeEntity(
            id = model.id,
            fullName = model.fullName,
            email = model.email,
            phone = model.phone,
            role = model.role.name,
            position = model.position.name,
            status = model.status.name,
            hourlyRate = model.hourlyRate,
            avatarColor = model.avatarColor,
            createdAt = model.createdAt
        )
    }
}

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val company: String,
    val phone: String,
    val email: String,
    val whatsapp: String,
    val instagram: String,
    val facebook: String,
    val address: String,
    val source: String,
    val status: String,
    val assignedEmployeeId: String,
    val notes: String,
    val createdAt: Long,
    val lastContactAt: Long
) {
    fun toModel(): Client = Client(
        id = id,
        name = name,
        company = company,
        phone = phone,
        email = email,
        whatsapp = whatsapp,
        instagram = instagram,
        facebook = facebook,
        address = address,
        source = try { LeadSource.valueOf(source) } catch (e: Exception) { LeadSource.GOOGLE },
        status = try { ClientStatus.valueOf(status) } catch (e: Exception) { ClientStatus.ACTIVE },
        assignedEmployeeId = assignedEmployeeId,
        notes = notes,
        createdAt = createdAt,
        lastContactAt = lastContactAt
    )

    companion object {
        fun fromModel(model: Client): ClientEntity = ClientEntity(
            id = model.id,
            name = model.name,
            company = model.company,
            phone = model.phone,
            email = model.email,
            whatsapp = model.whatsapp,
            instagram = model.instagram,
            facebook = model.facebook,
            address = model.address,
            source = model.source.name,
            status = model.status.name,
            assignedEmployeeId = model.assignedEmployeeId,
            notes = model.notes,
            createdAt = model.createdAt,
            lastContactAt = model.lastContactAt
        )
    }
}

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val projectId: String,
    val name: String,
    val clientId: String,
    val assignedEmployeeIds: String, // Comma-separated IDs
    val type: String,
    val startDate: Long,
    val deadline: Long,
    val agreedDeliveryTime: String,
    val priority: String,
    val budget: Double,
    val paymentStatus: String,
    val status: String,
    val progress: Int,
    val notes: String,
    val deliveredAt: Long?,
    val deliveredBy: String?,
    val deliveryNotes: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toModel(): Project = Project(
        projectId = projectId,
        name = name,
        clientId = clientId,
        assignedEmployeeIds = if (assignedEmployeeIds.isBlank()) emptyList() else assignedEmployeeIds.split(","),
        type = try { ProjectType.valueOf(type) } catch (e: Exception) { ProjectType.WEBSITE },
        startDate = startDate,
        deadline = deadline,
        agreedDeliveryTime = agreedDeliveryTime,
        priority = try { Priority.valueOf(priority) } catch (e: Exception) { Priority.MEDIUM },
        budget = budget,
        paymentStatus = paymentStatus,
        status = try { ProjectStatus.valueOf(status) } catch (e: Exception) { ProjectStatus.IN_PROGRESS },
        progress = progress,
        notes = notes,
        deliveredAt = deliveredAt,
        deliveredBy = deliveredBy,
        deliveryNotes = deliveryNotes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromModel(model: Project): ProjectEntity = ProjectEntity(
            projectId = model.projectId,
            name = model.name,
            clientId = model.clientId,
            assignedEmployeeIds = model.assignedEmployeeIds.joinToString(","),
            type = model.type.name,
            startDate = model.startDate,
            deadline = model.deadline,
            agreedDeliveryTime = model.agreedDeliveryTime,
            priority = model.priority.name,
            budget = model.budget,
            paymentStatus = model.paymentStatus,
            status = model.status.name,
            progress = model.progress,
            notes = model.notes,
            deliveredAt = model.deliveredAt,
            deliveredBy = model.deliveredBy,
            deliveryNotes = model.deliveryNotes,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt
        )
    }
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val projectId: String,
    val clientId: String,
    val assignedEmployeeId: String,
    val priority: String,
    val dueDate: Long,
    val status: String,
    val createdAt: Long,
    val completedAt: Long?
) {
    fun toModel(): Task = Task(
        id = id,
        title = title,
        description = description,
        projectId = projectId,
        clientId = clientId,
        assignedEmployeeId = assignedEmployeeId,
        priority = try { Priority.valueOf(priority) } catch (e: Exception) { Priority.MEDIUM },
        dueDate = dueDate,
        status = try { TaskStatus.valueOf(status) } catch (e: Exception) { TaskStatus.TODO },
        createdAt = createdAt,
        completedAt = completedAt
    )

    companion object {
        fun fromModel(model: Task): TaskEntity = TaskEntity(
            id = model.id,
            title = model.title,
            description = model.description,
            projectId = model.projectId,
            clientId = model.clientId,
            assignedEmployeeId = model.assignedEmployeeId,
            priority = model.priority.name,
            dueDate = model.dueDate,
            status = model.status.name,
            createdAt = model.createdAt,
            completedAt = model.completedAt
        )
    }
}

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val clientId: String,
    val projectId: String,
    val amount: Double,
    val currency: String,
    val paymentMethod: String,
    val paymentDate: Long,
    val status: String,
    val notes: String,
    val createdAt: Long,
    val createdBy: String
) {
    fun toModel(): Payment = Payment(
        id = id,
        clientId = clientId,
        projectId = projectId,
        amount = amount,
        currency = currency,
        paymentMethod = try { PaymentMethod.valueOf(paymentMethod) } catch (e: Exception) { PaymentMethod.BANK_TRANSFER },
        paymentDate = paymentDate,
        status = try { PaymentStatus.valueOf(status) } catch (e: Exception) { PaymentStatus.PENDING },
        notes = notes,
        createdAt = createdAt,
        createdBy = createdBy
    )

    companion object {
        fun fromModel(model: Payment): PaymentEntity = PaymentEntity(
            id = model.id,
            clientId = model.clientId,
            projectId = model.projectId,
            amount = model.amount,
            currency = model.currency,
            paymentMethod = model.paymentMethod.name,
            paymentDate = model.paymentDate,
            status = model.status.name,
            notes = model.notes,
            createdAt = model.createdAt,
            createdBy = model.createdBy
        )
    }
}

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val startMillis: Long,
    val endMillis: Long,
    val linkedClientId: String?,
    val linkedProjectId: String?,
    val assignedUserId: String?,
    val notes: String
) {
    fun toModel(): CalendarEvent = CalendarEvent(
        id = id,
        title = title,
        type = try { EventType.valueOf(type) } catch (e: Exception) { EventType.MEETING },
        startMillis = startMillis,
        endMillis = endMillis,
        linkedClientId = linkedClientId,
        linkedProjectId = linkedProjectId,
        assignedUserId = assignedUserId,
        notes = notes
    )

    companion object {
        fun fromModel(model: CalendarEvent): CalendarEventEntity = CalendarEventEntity(
            id = model.id,
            title = model.title,
            type = model.type.name,
            startMillis = model.startMillis,
            endMillis = model.endMillis,
            linkedClientId = model.linkedClientId,
            linkedProjectId = model.linkedProjectId,
            assignedUserId = model.assignedUserId,
            notes = model.notes
        )
    }
}

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val dateMillis: Long,
    val timeString: String,
    val userId: String,
    val relatedClientId: String?,
    val relatedProjectId: String?,
    val completed: Boolean
) {
    fun toModel(): Reminder = Reminder(
        id = id,
        title = title,
        description = description,
        dateMillis = dateMillis,
        timeString = timeString,
        userId = userId,
        relatedClientId = relatedClientId,
        relatedProjectId = relatedProjectId,
        completed = completed
    )

    companion object {
        fun fromModel(model: Reminder): ReminderEntity = ReminderEntity(
            id = model.id,
            title = model.title,
            description = model.description,
            dateMillis = model.dateMillis,
            timeString = model.timeString,
            userId = model.userId,
            relatedClientId = model.relatedClientId,
            relatedProjectId = model.relatedProjectId,
            completed = model.completed
        )
    }
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String,
    val title: String,
    val message: String,
    val relatedId: String?,
    val read: Boolean,
    val createdAt: Long
) {
    fun toModel(): NotificationItem = NotificationItem(
        id = id,
        userId = userId,
        type = type,
        title = title,
        message = message,
        relatedId = relatedId,
        read = read,
        createdAt = createdAt
    )

    companion object {
        fun fromModel(model: NotificationItem): NotificationEntity = NotificationEntity(
            id = model.id,
            userId = model.userId,
            type = model.type,
            title = model.title,
            message = model.message,
            relatedId = model.relatedId,
            read = model.read,
            createdAt = model.createdAt
        )
    }
}

@Entity(tableName = "communication_logs")
data class CommunicationLogEntity(
    @PrimaryKey val id: String,
    val clientId: String,
    val dateMillis: Long,
    val type: String,
    val employeeId: String,
    val note: String,
    val createdAt: Long
) {
    fun toModel(): CommunicationLog = CommunicationLog(
        id = id,
        clientId = clientId,
        dateMillis = dateMillis,
        type = try { CommunicationType.valueOf(type) } catch (e: Exception) { CommunicationType.EMAIL },
        employeeId = employeeId,
        note = note,
        createdAt = createdAt
    )

    companion object {
        fun fromModel(model: CommunicationLog): CommunicationLogEntity = CommunicationLogEntity(
            id = model.id,
            clientId = model.clientId,
            dateMillis = model.dateMillis,
            type = model.type.name,
            employeeId = model.employeeId,
            note = model.note,
            createdAt = model.createdAt
        )
    }
}

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val actorUserId: String,
    val actorName: String,
    val action: String,
    val entityType: String,
    val entityId: String,
    val description: String,
    val createdAt: Long
) {
    fun toModel(): ActivityLog = ActivityLog(
        id = id,
        actorUserId = actorUserId,
        actorName = actorName,
        action = action,
        entityType = entityType,
        entityId = entityId,
        description = description,
        createdAt = createdAt
    )

    companion object {
        fun fromModel(model: ActivityLog): ActivityLogEntity = ActivityLogEntity(
            id = model.id,
            actorUserId = model.actorUserId,
            actorName = model.actorName,
            action = model.action,
            entityType = model.entityType,
            entityId = model.entityId,
            description = model.description,
            createdAt = model.createdAt
        )
    }
}

@Entity(tableName = "project_files")
data class ProjectFileEntity(
    @PrimaryKey val id: String,
    val fileName: String,
    val storagePath: String,
    val downloadURL: String,
    val uploadedBy: String,
    val uploadedAt: Long,
    val fileSize: Long,
    val category: String,
    val projectId: String,
    val clientId: String
) {
    fun toModel(): ProjectFile = ProjectFile(
        id = id,
        fileName = fileName,
        storagePath = storagePath,
        downloadURL = downloadURL,
        uploadedBy = uploadedBy,
        uploadedAt = uploadedAt,
        fileSize = fileSize,
        category = try { FileCategory.valueOf(category) } catch (e: Exception) { FileCategory.DOCUMENTS },
        projectId = projectId,
        clientId = clientId
    )

    companion object {
        fun fromModel(model: ProjectFile): ProjectFileEntity = ProjectFileEntity(
            id = model.id,
            fileName = model.fileName,
            storagePath = model.storagePath,
            downloadURL = model.downloadURL,
            uploadedBy = model.uploadedBy,
            uploadedAt = model.uploadedAt,
            fileSize = model.fileSize,
            category = model.category.name,
            projectId = model.projectId,
            clientId = model.clientId
        )
    }
}

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val agencyName: String,
    val logoUrl: String,
    val currency: String,
    val language: String,
    val isDarkMode: Boolean,
    val defaultTaxRate: Double
) {
    fun toModel(): AgencySettings = AgencySettings(
        agencyName = agencyName,
        logoUrl = logoUrl,
        currency = currency,
        language = language,
        isDarkMode = isDarkMode,
        defaultTaxRate = defaultTaxRate
    )

    companion object {
        fun fromModel(model: AgencySettings): SettingsEntity = SettingsEntity(
            id = 1,
            agencyName = model.agencyName,
            logoUrl = model.logoUrl,
            currency = model.currency,
            language = model.language,
            isDarkMode = model.isDarkMode,
            defaultTaxRate = model.defaultTaxRate
        )
    }
}
