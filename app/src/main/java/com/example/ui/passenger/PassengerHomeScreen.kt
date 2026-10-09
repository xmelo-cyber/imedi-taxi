package com.example.ui.passenger

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.ChatMessage
import com.example.data.DriverVehicle
import com.example.data.Localization
import com.example.data.LocationPoint
import com.example.data.MockDatabase
import com.example.data.PaymentMethod
import com.example.data.PaymentType
import com.example.data.Ride
import com.example.data.RideStatus
import com.example.data.Tariff
import com.example.data.TariffType
import com.example.ui.components.InAppChatDialog
import com.example.ui.components.MapCanvas
import com.example.ui.components.MaskedCallDialog
import com.example.ui.components.ScheduleRideDialog
import com.example.ui.components.SosSafetyDialog
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TaxiYellow
import kotlinx.coroutines.delay

@Composable
fun PassengerHomeScreen(
    currentLang: AppLanguage,
    onOpenProfile: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeRide by MockDatabase.activeRide.collectAsState()
    val isDarkTheme by MockDatabase.isDarkTheme.collectAsState()
    val chatMessages by MockDatabase.chatMessages.collectAsState()

    // Location selection
    var pickupPoint by remember { mutableStateOf(MockDatabase.predefinedLocations[0]) }
    var dropoffPoint by remember { mutableStateOf<LocationPoint?>(MockDatabase.predefinedLocations[1]) }
    var stopsList by remember { mutableStateOf<List<LocationPoint>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isSelectingDestination by remember { mutableStateOf(false) }

    // Tariff and booking
    var selectedTariffType by remember { mutableStateOf(TariffType.ECONOMY) }
    var selectedPayment by remember { mutableStateOf(MockDatabase.defaultPaymentMethods[0]) }
    var promoCode by remember { mutableStateOf("") }
    var promoDiscount by remember { mutableDoubleStateOf(0.0) }
    var promoAppliedMessage by remember { mutableStateOf("") }

    // Dialogs
    var showChatDialog by remember { mutableStateOf(false) }
    var showCallDialog by remember { mutableStateOf(false) }
    var showSosDialog by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var scheduledTimeString by remember { mutableStateOf<String?>(null) }
    var showPaymentSelectorModal by remember { mutableStateOf(false) }
    var showAddStopModal by remember { mutableStateOf(false) }

    // Rating & Tips completed dialog
    var ratingStars by remember { mutableIntStateOf(5) }
    var selectedTip by remember { mutableDoubleStateOf(2.00) }
    var reviewComment by remember { mutableStateOf("") }

    // Ride state machine automatic simulated progression when order is placed
    LaunchedEffect(activeRide?.status) {
        val ride = activeRide
        if (ride != null) {
            when (ride.status) {
                RideStatus.SEARCHING -> {
                    delay(4000)
                    MockDatabase.updateRideStatus(RideStatus.DRIVER_ACCEPTED)
                }
                RideStatus.DRIVER_ACCEPTED -> {
                    delay(6000)
                    MockDatabase.updateRideStatus(RideStatus.DRIVER_ARRIVED)
                }
                RideStatus.DRIVER_ARRIVED -> {
                    delay(5000)
                    MockDatabase.updateRideStatus(RideStatus.IN_PROGRESS)
                }
                RideStatus.IN_PROGRESS -> {
                    delay(8000)
                    MockDatabase.updateRideStatus(RideStatus.COMPLETED)
                }
                else -> {}
            }
        }
    }

    // Fare calculation
    val currentTariff = MockDatabase.defaultTariffs.first { it.type == selectedTariffType }
    val estimatedDistance = 4.8 + (stopsList.size * 2.5)
    val estimatedDuration = 12 + (stopsList.size * 5)
    val rawFare = currentTariff.basePrice + (estimatedDistance * currentTariff.perKmPrice) + (estimatedDuration * currentTariff.perMinPrice)
    val finalCalculatedFare = maxOf(3.0, rawFare - promoDiscount)

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Interactive Map in Background
        MapCanvas(
            modifier = Modifier.fillMaxSize(),
            activeRide = activeRide,
            pickupLocation = pickupPoint,
            destinationLocation = dropoffPoint,
            isDarkMode = isDarkTheme
        )

        // 2. Top Header Navigation (Search, SOS, History)
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
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenProfile() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TaxiYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("დბ", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "დავით ბერიძე",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = TaxiYellow, modifier = Modifier.size(12.dp))
                            Text(" 4.96", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(
                        onClick = { showSosDialog = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(StatusRed.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.Security, contentDescription = "SOS", tint = StatusRed)
                    }
                }
            }
        }

        // 3. Bottom Sheet / Interactive Cards based on Ride Status
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            when {
                // CASE A: Ride Completed Modal (Rating & Tip)
                activeRide?.status == RideStatus.COMPLETED -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(36.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = Localization.get("trip_completed", currentLang),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "სულ გადასახდელი: ${String.format("%.2f", activeRide?.finalFare ?: finalCalculatedFare + selectedTip)} ₾",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 5 Star Rating
                            Text("როგორ ჩაიარა მგზავრობამ?", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (i in 1..5) {
                                    IconButton(onClick = { ratingStars = i }) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Star $i",
                                            tint = if (i <= ratingStars) TaxiYellow else Color.Gray,
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tips ("ჩაი")
                            Text("ჩაი მძღოლისთვის:", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf(0.0, 1.0, 2.0, 5.0).forEach { tip ->
                                    val isSelected = selectedTip == tip
                                    Card(
                                        onClick = { selectedTip = tip },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) TaxiYellow else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    ) {
                                        Text(
                                            text = if (tip == 0.0) "არა" else "+${tip.toInt()}₾",
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedTextField(
                                value = reviewComment,
                                onValueChange = { reviewComment = it },
                                placeholder = { Text("დატოვეთ კომენტარი (სურვილისამებრ)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    MockDatabase.rateAndCloseRide(ratingStars, selectedTip, reviewComment)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(Localization.get("done", currentLang), color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // CASE B: Searching Driver (Sonar Animation)
                activeRide?.status == RideStatus.SEARCHING -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val radarTransition = rememberInfiniteTransition(label = "radar")
                            val radarScale by radarTransition.animateFloat(
                                initialValue = 0.8f,
                                targetValue = 1.3f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1200, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "radarScale"
                            )

                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .scale(radarScale)
                                    .clip(CircleShape)
                                    .background(TaxiYellow.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(TaxiYellow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = Localization.get("searching_driver", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ვეძებთ უახლოეს მძღოლს საუკეთესო რეიტინგით",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            OutlinedButton(
                                onClick = { MockDatabase.updateRideStatus(RideStatus.CANCELLED) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed)
                            ) {
                                Text(Localization.get("cancel_order", currentLang), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // CASE C: Active Driver Assigned / Arrived / In Progress
                activeRide != null && (activeRide?.status == RideStatus.DRIVER_ACCEPTED || activeRide?.status == RideStatus.DRIVER_ARRIVED || activeRide?.status == RideStatus.IN_PROGRESS) -> {
                    val ride = activeRide!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            // Status Banner
                            val statusBannerText = when (ride.status) {
                                RideStatus.DRIVER_ACCEPTED -> "🚗 მძღოლი მოდის თქვენკენ (ჩამოსვლა ~ 3 წთ)"
                                RideStatus.DRIVER_ARRIVED -> "✅ მძღოლი ადგილზეა და გელოდებათ"
                                RideStatus.IN_PROGRESS -> "🛣️ მგზავრობა მიმდინარეობს"
                                else -> ""
                            }
                            Text(
                                text = statusBannerText,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Driver & Vehicle Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(TaxiYellow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("გმ", fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(ride.driverName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = TaxiYellow, modifier = Modifier.size(14.dp))
                                            Text(" ${ride.driverRating}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                            Text(" • 1,420 მგზავრობა", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                // Plate Badge
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(ride.vehicle.plateNumber, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        Text("${ride.vehicle.make} ${ride.vehicle.model}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Communication & SOS Action Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showCallDialog = true },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ზარი", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                                }

                                Button(
                                    onClick = { showChatDialog = true },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ჩატი", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                                }

                                Button(
                                    onClick = { showSosDialog = true },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed.copy(alpha = 0.2f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = StatusRed)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("SOS", color = StatusRed, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            TextButton(
                                onClick = { MockDatabase.updateRideStatus(RideStatus.CANCELLED) },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("მგზავრობის გაუქმება", color = StatusRed, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // CASE D: Default Order Setup (Destination Search, Tariffs, Payment, Order Button)
                else -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Pickup & Dropoff Fields
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    // Pickup Point
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(StatusGreen)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = pickupPoint.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Stops if any
                                    stopsList.forEachIndexed { index, stop ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(TaxiYellow)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "გაჩერება: ${stop.title}",
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { stopsList = stopsList.filterIndexed { i, _ -> i != index } },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Remove stop", modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }

                                    // Dropoff Point
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable { isSelectingDestination = true }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(StatusRed)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = dropoffPoint?.title ?: Localization.get("where_to", currentLang),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (dropoffPoint != null) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (dropoffPoint != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick destination tags & Add stop / Schedule row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showAddStopModal = true },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(Localization.get("add_stop", currentLang), fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { showScheduleDialog = true },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(scheduledTimeString ?: "დაგეგმვა", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tariff Carousel (Economy, Comfort, Business, Minivan)
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(MockDatabase.defaultTariffs) { tariff ->
                                    val isSelected = tariff.type == selectedTariffType
                                    val tariffTitle = when (currentLang) {
                                        AppLanguage.KA -> tariff.titleKa
                                        AppLanguage.EN -> tariff.titleEn
                                        AppLanguage.RU -> tariff.titleRu
                                    }
                                    val tariffFare = maxOf(3.0, (tariff.basePrice + (estimatedDistance * tariff.perKmPrice) + (estimatedDuration * tariff.perMinPrice)) - promoDiscount)

                                    Card(
                                        onClick = { selectedTariffType = tariff.type },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) TaxiYellow else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier
                                            .width(135.dp)
                                            .testTag("tariff_card_${tariff.type.name}")
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = tariffTitle,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = "${tariff.etaMinutes} წთ",
                                                    fontSize = 11.sp,
                                                    color = if (isSelected) Color.Black.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = "${String.format("%.2f", tariffFare)} ₾",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp,
                                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.primary
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = tariff.carExample,
                                                fontSize = 9.sp,
                                                maxLines = 1,
                                                color = if (isSelected) Color.Black.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Payment & Promo code strip
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Payment selector button
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { showPaymentSelectorModal = true }
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${selectedPayment.title} (${if (selectedPayment.cardLast4.isNotEmpty()) "•" + selectedPayment.cardLast4 else "ნაღდი"})",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                // Promo code pill
                                if (promoDiscount > 0.0) {
                                    Text(
                                        text = "-${promoDiscount.toInt()}₾ ფასდაკლება!",
                                        color = StatusGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    TextButton(onClick = { promoCode = "TBILISI2026"; promoDiscount = 2.0; promoAppliedMessage = "პრომოკოდი გააქტიურდა: -2.00₾" }) {
                                        Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("პრომოკოდი", fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Order Button
                            Button(
                                onClick = {
                                    val newRide = Ride(
                                        id = "ride_${System.currentTimeMillis()}",
                                        passengerName = "დავით ბერიძე",
                                        passengerPhone = "+995 599 12 34 56",
                                        passengerRating = 4.96,
                                        driverName = "გიორგი მამულაშვილი",
                                        driverPhone = "+995 577 88 99 00",
                                        driverRating = 4.94,
                                        vehicle = DriverVehicle("Toyota", "Prius IV", "თეთრი", "TX-101-GO", 2021),
                                        pickup = pickupPoint,
                                        dropoff = dropoffPoint ?: MockDatabase.predefinedLocations[1],
                                        stops = stopsList,
                                        tariff = selectedTariffType,
                                        distanceKm = estimatedDistance,
                                        durationMin = estimatedDuration,
                                        baseFare = currentTariff.basePrice,
                                        discount = promoDiscount,
                                        tip = 0.0,
                                        finalFare = finalCalculatedFare,
                                        status = RideStatus.SEARCHING,
                                        scheduledTime = scheduledTimeString,
                                        paymentMethod = selectedPayment,
                                        dateString = "ახლა"
                                    )
                                    MockDatabase.startRide(newRide)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("request_ride_button")
                            ) {
                                Text(
                                    text = "${Localization.get("order_taxi", currentLang)} • ${String.format("%.2f", finalCalculatedFare)} ₾",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ================= DESTINATION SELECTION MODAL =================
    if (isSelectingDestination) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { isSelectingDestination = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Localization.get("destination_point", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { isSelectingDestination = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("მისამართის ან ადგილის ძებნა...") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("პოპულარული და შენახული ადგილები:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredLocations = MockDatabase.predefinedLocations.filter {
                        searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.address.contains(searchQuery, ignoreCase = true)
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        filteredLocations.forEach { loc ->
                            Card(
                                onClick = {
                                    dropoffPoint = loc
                                    isSelectingDestination = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = when (loc.tag) {
                                        "HOME" -> Icons.Default.Home
                                        "WORK" -> Icons.Default.Work
                                        else -> Icons.Default.LocationOn
                                    }
                                    Icon(icon, contentDescription = null, tint = TaxiYellow, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(loc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(loc.address, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ================= ADD STOP MODAL =================
    if (showAddStopModal) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showAddStopModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("გაჩერების დამატება მარშრუტზე", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    MockDatabase.predefinedLocations.take(4).forEach { loc ->
                        Card(
                            onClick = {
                                stopsList = stopsList + loc
                                showAddStopModal = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = TaxiYellow)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(loc.title, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }

    // ================= PAYMENT METHOD PICKER MODAL =================
    if (showPaymentSelectorModal) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showPaymentSelectorModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("გადახდის მეთოდი", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("აირჩიეთ ან დაამატეთ ბარათი (BOG, TBC, Stripe)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    MockDatabase.defaultPaymentMethods.forEach { pm ->
                        val isSelected = selectedPayment.id == pm.id
                        Card(
                            onClick = {
                                selectedPayment = pm
                                showPaymentSelectorModal = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) TaxiYellow.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = TaxiYellow)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(pm.title, fontWeight = FontWeight.Bold)
                                        if (pm.cardLast4.isNotEmpty()) {
                                            Text("•••• •••• •••• ${pm.cardLast4}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = TaxiYellow)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            // Add bank card simulation
                            showPaymentSelectorModal = false
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+ ახალი ბარათის დამატება (BOG / TBC / Stripe)")
                    }
                }
            }
        }
    }

    // Dialog components
    if (showCallDialog) {
        MaskedCallDialog(
            contactName = activeRide?.driverName ?: "გიორგი მამულაშვილი",
            contactRole = "მძღოლი • Toyota Prius (TX-101-GO)",
            onDismiss = { showCallDialog = false }
        )
    }

    if (showChatDialog) {
        InAppChatDialog(
            messages = chatMessages,
            currentIsPassenger = true,
            onSendMessage = { MockDatabase.sendChatMessage(it, isPassenger = true) },
            onDismiss = { showChatDialog = false }
        )
    }

    if (showSosDialog) {
        SosSafetyDialog(
            rideId = activeRide?.id ?: "ride_live",
            onDismiss = { showSosDialog = false }
        )
    }

    if (showScheduleDialog) {
        ScheduleRideDialog(
            onConfirmSchedule = {
                scheduledTimeString = it
                showScheduleDialog = false
            },
            onDismiss = { showScheduleDialog = false }
        )
    }
}
