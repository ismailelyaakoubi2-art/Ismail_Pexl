package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgencyTopBar(
    title: String,
    currentUser: UserProfile,
    unreadNotifications: Int,
    isFirebaseReady: Boolean,
    onMenuClick: (() -> Unit)? = null,
    onRoleClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSearchClick: () -> Unit,
    onQuickActionClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            if (onMenuClick != null) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.testTag("top_bar_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Navigation Menu",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isFirebaseReady) EmeraldSuccess else AmberWarning)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isFirebaseReady) "Firebase Live" else "Offline First",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            // Quick action button
            IconButton(
                onClick = onQuickActionClick,
                modifier = Modifier.testTag("top_bar_quick_action")
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Quick Actions",
                    tint = IndigoPrimary
                )
            }

            // Search
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("top_bar_search")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Notification Bell with Badge
            BadgedBox(
                badge = {
                    if (unreadNotifications > 0) {
                        Badge(
                            containerColor = RoseError,
                            contentColor = Color.White
                        ) {
                            Text("$unreadNotifications")
                        }
                    }
                },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("top_bar_notifications")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Active Role Chip
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onRoleClick() }
                    .testTag("top_bar_role_chip"),
                color = when (currentUser.role) {
                    UserRole.ADMIN -> IndigoPrimary.copy(alpha = 0.15f)
                    UserRole.MANAGER -> CyanAccent.copy(alpha = 0.15f)
                    UserRole.EMPLOYEE -> EmeraldSuccess.copy(alpha = 0.15f)
                },
                border = BorderStroke(
                    1.dp,
                    when (currentUser.role) {
                        UserRole.ADMIN -> IndigoPrimary.copy(alpha = 0.4f)
                        UserRole.MANAGER -> CyanAccent.copy(alpha = 0.4f)
                        UserRole.EMPLOYEE -> EmeraldSuccess.copy(alpha = 0.4f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (currentUser.role) {
                            UserRole.ADMIN -> Icons.Default.Security
                            UserRole.MANAGER -> Icons.Default.SupervisorAccount
                            UserRole.EMPLOYEE -> Icons.Default.Person
                        },
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = when (currentUser.role) {
                            UserRole.ADMIN -> IndigoLight
                            UserRole.MANAGER -> CyanAccent
                            UserRole.EMPLOYEE -> EmeraldSuccess
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentUser.role.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (currentUser.role) {
                            UserRole.ADMIN -> IndigoLight
                            UserRole.MANAGER -> CyanAccent
                            UserRole.EMPLOYEE -> EmeraldSuccess
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun AgencyDrawerSheetContent(
    activeScreen: String,
    onSelectScreen: (String) -> Unit,
    currentUser: UserProfile,
    agencyName: String,
    unreadNotifications: Int,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.widthIn(max = 320.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 12.dp)
        ) {
            // Header Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = IndigoPrimary.copy(alpha = 0.12f)
                ),
                border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hexagon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = agencyName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${currentUser.fullName} (${currentUser.role.name})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MAIN OPERATIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 4.dp)
            )

            val mainNav = listOf(
                Triple("dashboard", "Dashboard", Icons.Outlined.Dashboard),
                Triple("clients", "Clients", Icons.Outlined.Business),
                Triple("projects", "Projects", Icons.Outlined.Folder),
                Triple("tasks", "Tasks (Kanban)", Icons.Outlined.Checklist),
                Triple("calendar", "Calendar & Reminders", Icons.Outlined.CalendarMonth),
                Triple("payments", "Payments & Finance", Icons.Outlined.AttachMoney),
                Triple("employees", "Team & Employees", Icons.Outlined.People)
            )

            mainNav.forEach { (route, label, icon) ->
                NavigationDrawerItem(
                    icon = { Icon(icon, contentDescription = null) },
                    label = { Text(label, fontSize = 13.sp, fontWeight = if (activeScreen == route) FontWeight.Bold else FontWeight.Medium) },
                    selected = activeScreen == route,
                    onClick = { onSelectScreen(route) },
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .testTag("drawer_item_$route"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = IndigoPrimary.copy(alpha = 0.15f),
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "INSIGHTS & SETTINGS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp)
            )

            val secondaryNav = listOf(
                Triple("reports", "Reports & Analytics", Icons.Outlined.BarChart),
                Triple("activity", "Global Activity Log", Icons.Outlined.History),
                Triple("notifications", "Notifications (${unreadNotifications})", Icons.Outlined.Notifications),
                Triple("settings", "Settings & Theme", Icons.Outlined.Settings)
            )

            secondaryNav.forEach { (route, label, icon) ->
                NavigationDrawerItem(
                    icon = {
                        if (route == "notifications" && unreadNotifications > 0) {
                            BadgedBox(badge = { Badge { Text("$unreadNotifications") } }) {
                                Icon(icon, contentDescription = null)
                            }
                        } else {
                            Icon(icon, contentDescription = null)
                        }
                    },
                    label = { Text(label, fontSize = 13.sp, fontWeight = if (activeScreen == route) FontWeight.Bold else FontWeight.Medium) },
                    selected = activeScreen == route,
                    onClick = { onSelectScreen(route) },
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .testTag("drawer_item_$route"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = IndigoPrimary.copy(alpha = 0.15f),
                        selectedIconColor = IndigoPrimary,
                        selectedTextColor = IndigoPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun RoleSwitchDialog(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Switch User Role",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Test the app across real permission levels enforced by Firestore Security Rules:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                RoleOptionCard(
                    title = "ADMIN (Full Access)",
                    description = "Manage all employees, clients, projects, tasks, payments, settings, and reports.",
                    icon = Icons.Default.Security,
                    isSelected = currentRole == UserRole.ADMIN,
                    color = IndigoPrimary
                ) {
                    onRoleSelected(UserRole.ADMIN)
                    onDismiss()
                }

                RoleOptionCard(
                    title = "MANAGER (Operations)",
                    description = "Manage clients, projects, tasks, view payments & reports. Restricted settings.",
                    icon = Icons.Default.SupervisorAccount,
                    isSelected = currentRole == UserRole.MANAGER,
                    color = CyanAccent
                ) {
                    onRoleSelected(UserRole.MANAGER)
                    onDismiss()
                }

                RoleOptionCard(
                    title = "EMPLOYEE (Restricted)",
                    description = "Only see assigned projects & tasks. Cannot view financial revenue or payments.",
                    icon = Icons.Default.Person,
                    isSelected = currentRole == UserRole.EMPLOYEE,
                    color = EmeraldSuccess
                ) {
                    onRoleSelected(UserRole.EMPLOYEE)
                    onDismiss()
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun RoleOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isSelected) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
        }
    }
}
