package com.example.ui.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.MockDatabase
import com.example.data.VerificationStatus
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TaxiYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    currentLang: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val driverApplications by MockDatabase.driverApplications.collectAsState()
    val activeRide by MockDatabase.activeRide.collectAsState()
    val rideHistory by MockDatabase.rideHistory.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Verification, 1: Tariffs & System, 2: Analytics
    var systemCommissionPercent by remember { mutableDoubleStateOf(12.0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TaxiYellow)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Taxigo ადმინ პანელი", fontWeight = FontWeight.Bold)
                    }
                },
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
        ) {
            // Admin Navigation Tabs
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = Color.Transparent,
                modifier = Modifier.clip(RoundedCornerShape(14.dp))
            ) {
                Tab(selected = selectedAdminTab == 0, onClick = { selectedAdminTab = 0 }, text = { Text("ვერიფიკაცია", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
                Tab(selected = selectedAdminTab == 1, onClick = { selectedAdminTab = 1 }, text = { Text("ტარიფები", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
                Tab(selected = selectedAdminTab == 2, onClick = { selectedAdminTab = 2 }, text = { Text("სტატისტიკა", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedAdminTab) {
                // TAB 0: Driver Verification Applications
                0 -> {
                    Text(
                        text = "ახალი მძღოლების განაცხადები (${driverApplications.size}):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(driverApplications) { app ->
                            val driverProf = app.driverProfile
                            val isPending = driverProf?.verificationStatus == VerificationStatus.PENDING

                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("${app.firstName} ${app.lastName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text(app.phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        Card(
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = when (driverProf?.verificationStatus) {
                                                    VerificationStatus.APPROVED -> StatusGreen.copy(alpha = 0.2f)
                                                    VerificationStatus.REJECTED -> StatusRed.copy(alpha = 0.2f)
                                                    else -> TaxiYellow.copy(alpha = 0.2f)
                                                }
                                            )
                                        ) {
                                            Text(
                                                text = when (driverProf?.verificationStatus) {
                                                    VerificationStatus.APPROVED -> "დამტკიცებული"
                                                    VerificationStatus.REJECTED -> "უარყოფილი"
                                                    else -> "განსახილველი"
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (driverProf?.vehicle != null) {
                                        Text(
                                            text = "ავტომობილი: ${driverProf.vehicle.make} ${driverProf.vehicle.model} (${driverProf.vehicle.plateNumber})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Documents list
                                    driverProf?.documents?.forEach { doc ->
                                        Text(
                                            text = "• ${doc.title}: ${doc.documentNumber} (ვადა: ${doc.expiryDate})",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (isPending) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { MockDatabase.rejectDriver(app.id) },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("უარყოფა", fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { MockDatabase.approveDriver(app.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("დამტკიცება", color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 1: Tariffs & System Commission
                1 -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("სისტემური კომისია", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("პლატფორმის წილი თითოეული მგზავრობიდან", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${systemCommissionPercent.toInt()}%", fontSize = 28.sp, fontWeight = FontWeight.Black, color = TaxiYellow)
                                Spacer(modifier = Modifier.width(16.dp))
                                Button(
                                    onClick = { systemCommissionPercent = if (systemCommissionPercent == 12.0) 10.0 else 12.0 },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Text("შეცვლა (10% / 12%)", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("მოქმედი ტარიფები:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(MockDatabase.defaultTariffs) { tariff ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(tariff.titleKa, fontWeight = FontWeight.Bold)
                                        Text("საბაზისო: ${tariff.basePrice}₾ • 1 კმ: ${tariff.perKmPrice}₾ • 1 წთ: ${tariff.perMinPrice}₾", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text("აქტიური", color = StatusGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // TAB 2: System Analytics & Financials
                2 -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Icon(Icons.Default.AttachMoney, contentDescription = null, tint = TaxiYellow)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("მთლიანი ბრუნვა", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("48,920 ₾", fontWeight = FontWeight.Black, fontSize = 20.sp)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Icon(Icons.Default.Percent, contentDescription = null, tint = StatusGreen)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("პლატფორმის მოგება", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("5,870 ₾", fontWeight = FontWeight.Black, fontSize = 20.sp, color = StatusGreen)
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = TaxiYellow)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("აქტიური მძღოლები", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("128 ონლაინ", fontWeight = FontWeight.Black, fontSize = 18.sp)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("სულ მგზავრები", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("4,612", fontWeight = FontWeight.Black, fontSize = 18.sp)
                                }
                            }
                        }

                        // Real-time server uptime & backend status card
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("სერვერის სტატუსი (Node.js & PostGIS):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("• WebSocket / Socket.IO: მუშაობს გამართულად (Uptime 99.98%)", fontSize = 11.sp, color = StatusGreen)
                                Text("• Redis Pub/Sub: აქტიურია (0.4ms latency)", fontSize = 11.sp, color = StatusGreen)
                                Text("• Google Maps Directions API: აქტიურია", fontSize = 11.sp, color = StatusGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}
