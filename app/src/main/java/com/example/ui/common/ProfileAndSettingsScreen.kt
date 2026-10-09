package com.example.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppLanguage
import com.example.data.Localization
import com.example.data.MockDatabase
import com.example.data.UserRole
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TaxiYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndSettingsScreen(
    currentLang: AppLanguage,
    onBack: () -> Unit,
    onOpenAdmin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by MockDatabase.currentUser.collectAsState()
    val isDarkTheme by MockDatabase.isDarkTheme.collectAsState()
    val notifications by MockDatabase.notifications.collectAsState()

    var showLanguageMenu by remember { mutableStateOf(false) }
    var showPrivacyModal by remember { mutableStateOf(false) }
    var showHelpModal by remember { mutableStateOf(false) }
    var showNotificationsModal by remember { mutableStateOf(false) }
    var pushNotificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("პროფილი და პარამეტრები", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // User Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(TaxiYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${currentUser.firstName.take(1)}${currentUser.lastName.take(1)}",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${currentUser.firstName} ${currentUser.lastName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentUser.phone,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentUser.email,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = TaxiYellow, modifier = Modifier.size(14.dp))
                            Text(" ${currentUser.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(" • ბალანსი: ${String.format("%.2f", currentUser.balance)} ₾", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Role Switching Section
            Text("როლის შეცვლა / რეჟიმები:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.SwapHoriz,
                        title = if (currentUser.role == UserRole.PASSENGER) Localization.get("switch_to_driver", currentLang) else Localization.get("switch_to_passenger", currentLang),
                        subtitle = "მიმდინარე როლი: ${if (currentUser.role == UserRole.PASSENGER) "მგზავრი" else "მძღოლი"}",
                        onClick = {
                            val nextRole = if (currentUser.role == UserRole.PASSENGER) UserRole.DRIVER else UserRole.PASSENGER
                            MockDatabase.switchRole(nextRole)
                            onBack()
                        }
                    )
                    HorizontalDivider()
                    SettingsRow(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "ადმინისტრატორის პანელი",
                        subtitle = "ვერიფიკაცია, ტარიფები, ანალიტიკა",
                        onClick = onOpenAdmin
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Settings Section
            Text("პარამეტრები:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Language
                    Box {
                        SettingsRow(
                            icon = Icons.Default.Language,
                            title = "ენა (Language)",
                            subtitle = currentLang.title,
                            onClick = { showLanguageMenu = true }
                        )
                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang.title) },
                                    onClick = {
                                        MockDatabase.setLanguage(lang)
                                        showLanguageMenu = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider()

                    // Dark Theme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("მუქი თემა (Dark Mode)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(if (isDarkTheme) "ჩართულია" else "გამორთულია", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { MockDatabase.toggleTheme() }
                        )
                    }

                    HorizontalDivider()

                    // Notifications Center
                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        title = "შეტყობინებების ცენტრი",
                        subtitle = "${notifications.size} შეტყობინება",
                        onClick = { showNotificationsModal = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Help, Privacy & Legal
            Text("მხარდაჭერა და უსაფრთხოება:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.HelpCenter,
                        title = "დახმარება & FAQ",
                        subtitle = "ხშირად დასმული კითხვები, ჩატი ოპერატორთან",
                        onClick = { showHelpModal = true }
                    )
                    HorizontalDivider()
                    SettingsRow(
                        icon = Icons.Default.Policy,
                        title = "კონფიდენციალურობის პოლიტიკა და წესები",
                        subtitle = "GDPR და პერსონალური მონაცემების დაცვა",
                        onClick = { showPrivacyModal = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout & Delete Account
            OutlinedButton(
                onClick = onLogout,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ანგარიშიდან გასვლა (Log Out)", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // ================= NOTIFICATIONS MODAL =================
    if (showNotificationsModal) {
        Dialog(onDismissRequest = { showNotificationsModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("შეტყობინებების ცენტრი", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))

                    notifications.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(item.time, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showNotificationsModal = false }, modifier = Modifier.align(Alignment.End)) {
                        Text("დახურვა")
                    }
                }
            }
        }
    }

    // ================= HELP & FAQ MODAL =================
    if (showHelpModal) {
        Dialog(onDismissRequest = { showHelpModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("დახმარების ცენტრი (FAQ)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    val faqs = listOf(
                        Pair("როგორ გამოვიძახო ტაქსი?", "რუკაზე აირჩიეთ დანიშნულების წერტილი, სასურველი ტარიფი და დააჭირეთ 'ტაქსის გამოძახება'."),
                        Pair("როგორ ხდება გადახდა?", "შეგიძლიათ გადაიხადოთ ნაღდი ფულით ან დაამატოთ BOG / TBC / საერთაშორისო ბარათი."),
                        Pair("როგორ გავხდე Taxigo-ს მძღოლი?", "რეგისტრაციისას მონიშნეთ 'მე ვარ მძღოლი', ატვირთეთ მართვის მოწმობა და ტექ-პასპორტი.")
                    )

                    faqs.forEach { (q, a) ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text("Q: $q", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("A: $a", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showHelpModal = false }, modifier = Modifier.align(Alignment.End)) {
                        Text("დახურვა")
                    }
                }
            }
        }
    }

    // ================= PRIVACY & TERMS MODAL =================
    if (showPrivacyModal) {
        Dialog(onDismissRequest = { showPrivacyModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("კონფიდენციალურობის პოლიტიკა (GDPR)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Taxigo იცავს თქვენს პერსონალურ მონაცემებს საერთაშორისო GDPR და ადგილობრივი კანონმდებლობის შესაბამისად.\n\n" +
                                "1. ტელეფონის ნომრები დაფარულია (Masked) - მგზავრი და მძღოლი ერთმანეთს უკავშირდებიან უსაფრთხო პროქსით.\n" +
                                "2. გეოლოკაცია გამოიყენება მხოლოდ აქტიური მგზავრობის უსაფრთხოებისა და მარშრუტის გამოთვლისთვის.\n" +
                                "3. გადახდის მონაცემები დაშიფრულია PCI-DSS სტანდარტით (BOG iPay / TBC Pay / Stripe).",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { showPrivacyModal = false }, modifier = Modifier.align(Alignment.End)) {
                        Text("გავიგე")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
