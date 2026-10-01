package com.example.model

enum class UserRole {
    ADMIN,
    MANAGER,
    EMPLOYEE
}

enum class UserPosition(val title: String) {
    MANAGER("Manager"),
    DESIGNER("Designer"),
    VIDEO_EDITOR("Video Editor"),
    DEVELOPER("Developer"),
    MARKETING("Marketing"),
    SALES("Sales"),
    CONTENT_CREATOR("Content Creator"),
    ACCOUNT_MANAGER("Account Manager")
}

enum class UserStatus(val label: String) {
    ACTIVE("Active"),
    AWAY("Away"),
    INACTIVE("Inactive")
}

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val photoURL: String = "",
    val role: UserRole = UserRole.ADMIN,
    val position: UserPosition = UserPosition.MANAGER,
    val status: UserStatus = UserStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

data class Employee(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.EMPLOYEE,
    val position: UserPosition = UserPosition.DESIGNER,
    val status: UserStatus = UserStatus.ACTIVE,
    val hourlyRate: Double = 35.0,
    val avatarColor: Long = 0xFF6366F1,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ClientStatus(val label: String) {
    LEAD("Lead"),
    PROSPECT("Prospect"),
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    VIP("VIP")
}

enum class LeadSource(val label: String) {
    FACEBOOK_ADS("Facebook Ads"),
    INSTAGRAM("Instagram"),
    TIKTOK("TikTok"),
    GOOGLE("Google"),
    WEBSITE("Website"),
    WHATSAPP("WhatsApp"),
    REFERRAL("Referral"),
    EXISTING_CLIENT("Existing Client"),
    OTHER("Other")
}

enum class ClientHealth(val label: String) {
    GOOD("Good"),
    ATTENTION("Attention"),
    AT_RISK("At Risk")
}

data class ClientHealthResult(
    val health: ClientHealth,
    val reasons: List<String>
)

data class Client(
    val id: String = "",
    val name: String = "",
    val company: String = "",
    val phone: String = "",
    val email: String = "",
    val whatsapp: String = "",
    val instagram: String = "",
    val facebook: String = "",
    val address: String = "",
    val source: LeadSource = LeadSource.GOOGLE,
    val status: ClientStatus = ClientStatus.ACTIVE,
    val assignedEmployeeId: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastContactAt: Long = System.currentTimeMillis()
)

enum class ProjectType(val label: String) {
    SOCIAL_MEDIA("Social Media"),
    VIDEO_EDITING("Video Editing"),
    UGC("UGC"),
    ADVERTISING("Advertising"),
    WEBSITE("Website"),
    BRANDING("Branding"),
    GRAPHIC_DESIGN("Graphic Design"),
    OTHER("Other")
}

enum class ProjectStatus(val label: String) {
    NEW("New"),
    IN_PROGRESS("In Progress"),
    WAITING_CLIENT("Waiting Client"),
    REVIEW("Review"),
    APPROVED("Approved"),
    DELIVERED("Delivered"),
    COMPLETED("Completed"),
    DELAYED("Delayed"),
    CANCELLED("Cancelled")
}

enum class Priority(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent")
}

data class Project(
    val projectId: String = "",
    val name: String = "",
    val clientId: String = "",
    val assignedEmployeeIds: List<String> = emptyList(),
    val type: ProjectType = ProjectType.WEBSITE,
    val startDate: Long = System.currentTimeMillis(),
    val deadline: Long = System.currentTimeMillis() + 7 * 86400000L,
    val agreedDeliveryTime: String = "18:00",
    val priority: Priority = Priority.MEDIUM,
    val budget: Double = 1500.0,
    val paymentStatus: String = "Pending",
    val status: ProjectStatus = ProjectStatus.IN_PROGRESS,
    val progress: Int = 20, // 0 - 100
    val notes: String = "",
    val deliveredAt: Long? = null,
    val deliveredBy: String? = null,
    val deliveryNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class TaskStatus(val label: String) {
    TODO("Todo"),
    IN_PROGRESS("In Progress"),
    REVIEW("Review"),
    DONE("Done")
}

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val projectId: String = "",
    val clientId: String = "",
    val assignedEmployeeId: String = "",
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long = System.currentTimeMillis() + 3 * 86400000L,
    val status: TaskStatus = TaskStatus.TODO,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

enum class PaymentStatus(val label: String) {
    PAID("Paid"),
    PARTIALLY_PAID("Partially Paid"),
    PENDING("Pending"),
    OVERDUE("Overdue")
}

enum class PaymentMethod(val label: String) {
    CASH("Cash"),
    BANK_TRANSFER("Bank Transfer"),
    PAYPAL("PayPal"),
    STRIPE("Stripe"),
    OTHER("Other")
}

data class Payment(
    val id: String = "",
    val clientId: String = "",
    val projectId: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val paymentMethod: PaymentMethod = PaymentMethod.BANK_TRANSFER,
    val paymentDate: Long = System.currentTimeMillis(),
    val status: PaymentStatus = PaymentStatus.PENDING,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String = ""
)

enum class EventType(val label: String) {
    PROJECT_DEADLINE("Project Deadline"),
    TASK_DEADLINE("Task Deadline"),
    MEETING("Meeting"),
    CLIENT_CALL("Client Call"),
    DELIVERY("Delivery"),
    REMINDER("Reminder")
}

data class CalendarEvent(
    val id: String = "",
    val title: String = "",
    val type: EventType = EventType.MEETING,
    val startMillis: Long = System.currentTimeMillis(),
    val endMillis: Long = System.currentTimeMillis() + 3600000L,
    val linkedClientId: String? = null,
    val linkedProjectId: String? = null,
    val assignedUserId: String? = null,
    val notes: String = ""
)

data class Reminder(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val timeString: String = "10:00",
    val userId: String = "",
    val relatedClientId: String? = null,
    val relatedProjectId: String? = null,
    val completed: Boolean = false
)

data class NotificationItem(
    val id: String = "",
    val userId: String = "",
    val type: String = "info",
    val title: String = "",
    val message: String = "",
    val relatedId: String? = null,
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class CommunicationType(val label: String) {
    WHATSAPP("WhatsApp"),
    EMAIL("Email"),
    PHONE("Phone"),
    MEETING("Meeting"),
    INSTAGRAM("Instagram"),
    OTHER("Other")
}

data class CommunicationLog(
    val id: String = "",
    val clientId: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val type: CommunicationType = CommunicationType.EMAIL,
    val employeeId: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class ActivityLog(
    val id: String = "",
    val actorUserId: String = "",
    val actorName: String = "",
    val action: String = "",
    val entityType: String = "",
    val entityId: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class FileCategory(val label: String) {
    BRIEF("Brief"),
    IMAGES("Images"),
    VIDEOS("Videos"),
    DOCUMENTS("Documents"),
    CONTRACT("Contract"),
    FINAL_FILES("Final Files")
}

data class ProjectFile(
    val id: String = "",
    val fileName: String = "",
    val storagePath: String = "",
    val downloadURL: String = "",
    val uploadedBy: String = "",
    val uploadedAt: Long = System.currentTimeMillis(),
    val fileSize: Long = 1024 * 1024L,
    val category: FileCategory = FileCategory.DOCUMENTS,
    val projectId: String = "",
    val clientId: String = ""
)

data class AgencySettings(
    val agencyName: String = "OmniAgency Digital",
    val logoUrl: String = "",
    val currency: String = "USD",
    val language: String = "en", // "en", "fr", "ar"
    val isDarkMode: Boolean = true,
    val defaultTaxRate: Double = 0.0
)
