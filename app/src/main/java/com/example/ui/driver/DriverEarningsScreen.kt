package com.example.ui.driver

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AppLanguage
import com.example.data.MockDatabase
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TaxiYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverEarningsScreen(
    currentLang: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by MockDatabase.currentUser.collectAsState()
    val driverProfile = currentUser.driverProfile

    var selectedPeriodTab by remember { mutableIntStateOf(0) } // 0: Today, 1: Week, 2: Month
    var showWithdrawModal by remember { mutableStateOf(false) }
    var withdrawSuccessMsg by remember { mutableStateOf("") }
    var withdrawAmount by remember { mutableStateOf("50.00") }

    val periodEarnings = when (selectedPeriodTab) {
        0 -> driverProfile?.todayEarnings ?: 86.40
        1 -> driverProfile?.weeklyEarnings ?: 530.00
        else -> driverProfile?.monthlyEarnings ?: 2150.00
    }
    val periodRides = when (selectedPeriodTab) {
        0 -> 8
        1 -> 46
        else -> 192
    }
    val commissionPaid = periodEarnings * ((driverProfile?.commissionPercent ?: 12.0) / 100.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("შემოსავლები და ბალანსი", fontWeight = FontWeight.Bold) },
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
            // Period Selector Tabs
            TabRow(
                selectedTabIndex = selectedPeriodTab,
                containerColor = Color.Transparent,
                modifier = Modifier.clip(RoundedCornerShape(14.dp))
            ) {
                Tab(selected = selectedPeriodTab == 0, onClick = { selectedPeriodTab = 0 }, text = { Text("დღეს", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedPeriodTab == 1, onClick = { selectedPeriodTab = 1 }, text = { Text("კვირა", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedPeriodTab == 2, onClick = { selectedPeriodTab = 2 }, text = { Text("თვე", fontWeight = FontWeight.Bold) })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Earnings Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "სუფთა შემოსავალი",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${String.format("%.2f", periodEarnings - commissionPaid)} ₾",
                        fontWeight = FontWeight.Black,
                        fontSize = 36.sp,
                        color = TaxiYellow
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("მთლიანი", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${String.format("%.2f", periodEarnings)} ₾", fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("კომისია (12%)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("-${String.format("%.2f", commissionPaid)} ₾", fontWeight = FontWeight.Bold, color = StatusRed)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("შეკვეთები", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$periodRides", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wallet Actions: Cash-out & Top-up
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showWithdrawModal = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("თანხის გატანა", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { /* simulated top up */ },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("შევსება", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("დღევანდელი მგზავრობები:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Recent trips earnings ledger
            val dummyDayRides = listOf(
                Pair("თავისუფლების მოედ. -> ვაკე", "+11.20 ₾ • 14:15"),
                Pair("რუსთაველი -> თბილისი მოლი", "+18.50 ₾ • 13:02"),
                Pair("მარჯანიშვილი -> საბურთალო", "+8.40 ₾ • 11:45"),
                Pair("ავლაბარი -> ძველი თბილისი", "+5.50 ₾ • 10:20")
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dummyDayRides) { (route, info) ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(StatusGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(route, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(info.substringAfter("• "), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text(info.substringBefore(" •"), fontWeight = FontWeight.Bold, color = StatusGreen)
                        }
                    }
                }
            }
        }
    }

    // Withdraw Dialog
    if (showWithdrawModal) {
        Dialog(onDismissRequest = { showWithdrawModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("თანხის გატანა საბანკო ბარათზე", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("თანხა მყისიერად ჩაირიცხება თქვენს BOG/TBC ბარათზე", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        label = { Text("თანხა (₾)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (withdrawSuccessMsg.isNotEmpty()) {
                        Text(withdrawSuccessMsg, color = StatusGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            withdrawSuccessMsg = "მოთხოვნა შესრულდა! $withdrawAmount ₾ გადარიცხულია."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("გადარიცხვა", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showWithdrawModal = false }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("დახურვა")
                    }
                }
            }
        }
    }
}
