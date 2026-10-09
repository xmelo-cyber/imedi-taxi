package com.example.data

enum class AppLanguage(val code: String, val title: String) {
    KA("ka", "ქართული"),
    EN("en", "English"),
    RU("ru", "Русский")
}

enum class UserRole {
    PASSENGER,
    DRIVER,
    ADMIN
}

enum class VerificationStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class DriverDocument(
    val title: String,
    val type: String,
    val status: VerificationStatus,
    val documentNumber: String,
    val expiryDate: String
)

data class DriverVehicle(
    val make: String,
    val model: String,
    val color: String,
    val plateNumber: String,
    val year: Int
)

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String = "",
    val rating: Double = 4.95,
    val balance: Double = 120.50,
    val driverProfile: DriverProfile? = null
)

data class DriverProfile(
    val isOnline: Boolean = true,
    val vehicle: DriverVehicle,
    val documents: List<DriverDocument>,
    val verificationStatus: VerificationStatus = VerificationStatus.APPROVED,
    val completedRides: Int = 1420,
    val rating: Double = 4.92,
    val todayEarnings: Double = 86.40,
    val weeklyEarnings: Double = 530.00,
    val monthlyEarnings: Double = 2150.00,
    val commissionPercent: Double = 12.0
)

data class LocationPoint(
    val title: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val tag: String = "" // "HOME", "WORK", "RECENT"
)

enum class TariffType {
    ECONOMY,
    COMFORT,
    BUSINESS,
    MINIVAN
}

data class Tariff(
    val type: TariffType,
    val titleKa: String,
    val titleEn: String,
    val titleRu: String,
    val basePrice: Double,
    val perKmPrice: Double,
    val perMinPrice: Double,
    val etaMinutes: Int,
    val carExample: String
)

enum class PaymentType {
    CASH,
    BOG_IPAY,
    TBC_PAY,
    STRIPE_CARD
}

data class PaymentMethod(
    val id: String,
    val type: PaymentType,
    val title: String,
    val cardLast4: String = "",
    val isDefault: Boolean = false
)

enum class RideStatus {
    SEARCHING,
    DRIVER_ACCEPTED,
    DRIVER_ARRIVED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class Ride(
    val id: String,
    val passengerName: String,
    val passengerPhone: String,
    val passengerRating: Double,
    val driverName: String,
    val driverPhone: String,
    val driverRating: Double,
    val vehicle: DriverVehicle,
    val pickup: LocationPoint,
    val dropoff: LocationPoint,
    val stops: List<LocationPoint> = emptyList(),
    val tariff: TariffType,
    val distanceKm: Double,
    val durationMin: Int,
    val baseFare: Double,
    val discount: Double = 0.0,
    val tip: Double = 0.0,
    val finalFare: Double,
    val status: RideStatus,
    val scheduledTime: String? = null,
    val paymentMethod: PaymentMethod,
    val userRating: Int? = null,
    val userReview: String? = null,
    val dateString: String = "დღეს, 14:30"
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val isFromPassenger: Boolean,
    val text: String,
    val time: String
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)
