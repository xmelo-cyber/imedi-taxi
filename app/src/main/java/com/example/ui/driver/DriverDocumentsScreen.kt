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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Text
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
fun DriverDocumentsScreen(
    currentLang: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by MockDatabase.currentUser.collectAsState()
    val driverProfile = currentUser.driverProfile
    val documents = driverProfile?.documents ?: emptyList()
    var uploadStatusMsg by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("მძღოლის დოკუმენტები", fontWeight = FontWeight.Bold) },
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
            // Vehicle Info Card
            if (driverProfile?.vehicle != null) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(TaxiYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "${driverProfile.vehicle.make} ${driverProfile.vehicle.model} (${driverProfile.vehicle.year})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "სახ. ნომერი: ${driverProfile.vehicle.plateNumber} • ${driverProfile.vehicle.color}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("რეგისტრირებული დოკუმენტები:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(documents) { doc ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (doc.status) {
                                                VerificationStatus.APPROVED -> StatusGreen.copy(alpha = 0.2f)
                                                VerificationStatus.PENDING -> TaxiYellow.copy(alpha = 0.2f)
                                                VerificationStatus.REJECTED -> StatusRed.copy(alpha = 0.2f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (doc.status) {
                                            VerificationStatus.APPROVED -> Icons.Default.CheckCircle
                                            VerificationStatus.PENDING -> Icons.Default.HourglassEmpty
                                            VerificationStatus.REJECTED -> Icons.Default.Warning
                                        },
                                        contentDescription = null,
                                        tint = when (doc.status) {
                                            VerificationStatus.APPROVED -> StatusGreen
                                            VerificationStatus.PENDING -> TaxiYellow
                                            VerificationStatus.REJECTED -> StatusRed
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("ნომერი: ${doc.documentNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("ვადა: ${doc.expiryDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            // Status Pill
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when (doc.status) {
                                        VerificationStatus.APPROVED -> StatusGreen.copy(alpha = 0.2f)
                                        VerificationStatus.PENDING -> TaxiYellow.copy(alpha = 0.2f)
                                        VerificationStatus.REJECTED -> StatusRed.copy(alpha = 0.2f)
                                    }
                                )
                            ) {
                                Text(
                                    text = when (doc.status) {
                                        VerificationStatus.APPROVED -> "დამტკიცებული"
                                        VerificationStatus.PENDING -> "მოლოდინში"
                                        VerificationStatus.REJECTED -> "უარყოფილი"
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (doc.status) {
                                        VerificationStatus.APPROVED -> StatusGreen
                                        VerificationStatus.PENDING -> TaxiYellow
                                        VerificationStatus.REJECTED -> StatusRed
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (uploadStatusMsg.isNotEmpty()) {
                Text(uploadStatusMsg, color = StatusGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
            }

            Button(
                onClick = {
                    uploadStatusMsg = "ფოტო აიტვირთა და გადაეცა ადმინისტრაციას განსახილველად!"
                },
                colors = ButtonDefaults.buttonColors(containerColor = TaxiYellow),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ახალი დოკუმენტის ან ფოტოს ატვირთვა", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
