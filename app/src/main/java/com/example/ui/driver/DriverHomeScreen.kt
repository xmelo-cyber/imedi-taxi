package com.example.ui.driver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppLanguage
import com.example.data.Localization
import com.example.data.MockDatabase
import com.example.data.Ride
import com.example.data.RideStatus
import com.example.ui.components.InAppChatDialog
import com.example.ui.components.MapCanvas
import com.example.ui.components.MaskedCallDialog
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TaxiYellow
import kotlinx.coroutines.delay

@Composable
fun DriverHomeScreen(
    currentLang: AppLanguage,
    onOpenEarnings: () -> Unit,
    onOpenDocuments: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOnline by MockDatabase.isDriverOnline.collectAsState()
    val activeRide by MockDatabase.activeRide.collectAsState()
    val incomingOrder by MockDatabase.driverIncomingOrder.collectAsState()
    val isDarkTheme by MockDatabase.isDarkTheme.collectAsState()
    val chatMessages by MockDatabase.chatMessages.collectAsState()

    // Dialogs
    var showChatDialog by remember { mutableStateOf(false) }
    var showCallDialog by remember { mutableStateOf(false) }

    // Taximeter state during active trip
    var meterDistance by remember { mutableDoubleStateOf(0.0) }
    var meterDurationSec by remember { mutableIntStateOf(0) }
    var meterFare by remember { mutableDoubleStateOf(4.00) }

    // Incoming order timer countdown (15s)
    var incomingTimer by remember { mutableIntStateOf(15) }

    // Live taximeter tick
    LaunchedEffect(activeRide?.status) {
        if (activeRide?.status == RideStatus.IN_PROGRESS) {
            meterDistance = 0.5
            meterDurationSec = 0
            meterFare = activeRide?.baseFare ?: 4.00
            while (activeRide?.status == RideStatus.IN_PROGRESS) {
                delay(1000)
                meterDurationSec++
                meterDistance += 0.04
                meterFare += 0.08
            }
        }
    }

    // Auto trigger test order if driver is online and has no orders
    LaunchedEffect(isOnline, activeRide, incomingOrder) {
        if (isOnline && activeRide == null && incomingOrder == null) {
            delay(5000)
            MockDatabase.triggerSimulatedDriverOrder()
            incomingTimer = 15
        }
    }

    // Countdown for incoming order
    LaunchedEffect(incomingOrder) {
        if (incomingOrder != null) {
            incomingTimer = 15
            while (incomingTimer > 0 && incomingOrder != null) {
                delay(1000)
                incomingTimer--
            }
            if (incomingTimer <= 0) {
                MockDatabase.declineIncomingDriverOrder()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Map in Background
        MapCanvas(
            modifier = Modifier.fillMaxSize(),
            activeRide = activeRide,
            isDarkMode = isDarkTheme
        )

        // 2. Top Driver Header: Online Toggle, Quick Stats, Navigation
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) StatusGreen else StatusRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOnline) Localization.get("online", currentLang) else Localization.get("offline", currentLang),
                        fontWeight = FontWeight.Bold,
                        color = if (isOnline) StatusGreen else StatusRed,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isOnline,
                        onCheckedChange = { MockDatabase.toggleDriverOnline() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = StatusGreen
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenEarnings) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Earnings", tint = TaxiYellow)
                    }
                    IconButton(onClick = onOpenDocuments) {
                        Icon(Icons.Default.Description, contentDescription = "Documents", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = onOpenProfile) {
                        Icon(Icons.Default.Navigation, contentDescription = "Profile", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // 3. Driver Bottom Action Sheet based on state
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            when {
                // Active Ride State Progression for Driver
                activeRide != null -> {
                    val ride = activeRide!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Status Title & Passenger Info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = ride.passengerName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = TaxiYellow, modifier = Modifier.size(14.dp))
                                        Text(" ${ride.passengerRating}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                        Text(" • გადახდა: ${ride.paymentMethod.title}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = { showCallDialog = true },
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { showChatDialog = true },
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = "Chat", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Route address strip
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusGreen))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(ride.pickup.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusRed))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(ride.dropoff.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Live Taximeter Display when in progress
                            if (ride.status == RideStatus.IN_PROGRESS) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = TaxiYellow.copy(alpha = 0.15f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("ტაქსომეტრი", style = MaterialTheme.typography.labelSmall)
                                            Text("${String.format("%.2f", meterFare)} ₾", fontWeight = FontWeight.Black, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("მანძილი", style = MaterialTheme.typography.labelSmall)
                                            Text("${String.format("%.1f", meterDistance)} კმ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            val mins = meterDurationSec / 60
                                            val secs = meterDurationSec % 60
                                            Text("დრო", style = MaterialTheme.typography.labelSmall)
                                            Text(String.format("%02d:%02d", mins, secs), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            // Dynamic Action Progression Button:
                            when (ride.status) {
                                RideStatus.DRIVER_ACCEPTED -> {
                                    Button(
                                        onClick = { MockDatabase.updateRideStatus(RideStatus.DRIVER_ARRIVED) },
                                        colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                    ) {
                                        Text(
                                            text = Localization.get("arrived_btn", currentLang),
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                RideStatus.DRIVER_ARRIVED -> {
                                    Button(
                                        onClick = { MockDatabase.updateRideStatus(RideStatus.IN_PROGRESS) },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                    ) {
                                        Text(
                                            text = Localization.get("start_trip_btn", currentLang),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                RideStatus.IN_PROGRESS -> {
                                    Button(
                                        onClick = { MockDatabase.updateRideStatus(RideStatus.COMPLETED) },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                    ) {
                                        Text(
                                            text = Localization.get("complete_trip_btn", currentLang),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }

                // Default Waiting Mode for Driver
                else -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.BatteryChargingFull,
                                        contentDescription = null,
                                        tint = StatusGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("GPS & ბატარეა: ოპტიმიზებული", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("დღევანდელი: 86.40 ₾", fontWeight = FontWeight.Bold, color = TaxiYellow)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (isOnline) {
                                Text(
                                    text = "📡 სისტემა ეძებს ახალ შეკვეთებს თქვენს სიახლოვეს...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { MockDatabase.triggerSimulatedDriverOrder() },
                                    colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow.copy(alpha = 0.3f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("ტესტური შეკვეთის გამოძახება 🔔", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                                }
                            } else {
                                Text(
                                    text = "თქვენ ოფლაინში ხართ. ჩართეთ გადამრთველი შეკვეთების მისაღებად.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ================= INCOMING ORDER POPUP (15s COUNTDOWN) =================
    if (incomingOrder != null) {
        val order = incomingOrder!!
        Dialog(onDismissRequest = { /* forced action */ }) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Localization.get("incoming_order", currentLang),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TaxiYellow
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StatusRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$incomingTimer",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { incomingTimer / 15f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = TaxiYellow
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${String.format("%.2f", order.finalFare)} ₾",
                        fontWeight = FontWeight.Black,
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ტარიფი: ${order.tariff.name} • მანძილი: ${order.distanceKm} კმ (~${order.durationMin} წთ)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusGreen))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("საიდან: ${order.pickup.title}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusRed))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("სად: ${order.dropoff.title}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { MockDatabase.declineIncomingDriverOrder() },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed)
                        ) {
                            Text(Localization.get("decline", currentLang), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { MockDatabase.acceptIncomingDriverOrder() },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
                        ) {
                            Text(Localization.get("accept", currentLang), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    // Call and chat dialogs
    if (showCallDialog) {
        MaskedCallDialog(
            contactName = activeRide?.passengerName ?: "მგზავრი",
            contactRole = "მგზავრი (ნომერი დაცულია)",
            onDismiss = { showCallDialog = false }
        )
    }

    if (showChatDialog) {
        InAppChatDialog(
            messages = chatMessages,
            currentIsPassenger = false,
            onSendMessage = { MockDatabase.sendChatMessage(it, isPassenger = false) },
            onDismiss = { showChatDialog = false }
        )
    }
}
