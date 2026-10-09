package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MockDatabase {

    val predefinedLocations = listOf(
        LocationPoint("რუსთაველის გამზ. 24", "რუსთაველის გამზირი 24, თბილისი", 41.6975, 44.7985, "HOME"),
        LocationPoint("თავისუფლების მოედანი", "თავისუფლების მოედანი 1, თბილისი", 41.6934, 44.8015, "WORK"),
        LocationPoint("ვაკის პარკი", "ი. ჭავჭავაძის გამზ. 74, თბილისი", 41.7112, 44.7598, "RECENT"),
        LocationPoint("მარჯანიშვილის მოედანი", "კ. მარჯანიშვილის ქუჩა, თბილისი", 41.7090, 44.8020, "RECENT"),
        LocationPoint("თბილისის საერთაშორისო აეროპორტი", "აეროპორტის გზატკეცილი, თბილისი", 41.6690, 44.9540, "AIRPORT"),
        LocationPoint("თბილისი მოლი", "დ. აღმაშენებლის ხეივანი 16-ე კმ", 41.7850, 44.7700, "RECENT"),
        LocationPoint("სითი მოლი საბურთალო", "ვაჟა-ფშაველას გამზ. 70, თბილისი", 41.7250, 44.7400, "RECENT"),
        LocationPoint("ძველი თბილისი / აბანოთუბანი", "აბანოს ქუჩა, თბილისი", 41.6880, 44.8100, "RECENT")
    )

    val defaultTariffs = listOf(
        Tariff(
            type = TariffType.ECONOMY,
            titleKa = "ეკონომი",
            titleEn = "Economy",
            titleRu = "Эконом",
            basePrice = 2.50,
            perKmPrice = 0.80,
            perMinPrice = 0.15,
            etaMinutes = 3,
            carExample = "Toyota Prius, Hyundai Ioniq"
        ),
        Tariff(
            type = TariffType.COMFORT,
            titleKa = "კომფორტი",
            titleEn = "Comfort",
            titleRu = "Комфорт",
            basePrice = 4.00,
            perKmPrice = 1.10,
            perMinPrice = 0.20,
            etaMinutes = 5,
            carExample = "Toyota Camry, Škoda Octavia"
        ),
        Tariff(
            type = TariffType.BUSINESS,
            titleKa = "ბიზნესი",
            titleEn = "Business",
            titleRu = "Бизнес",
            basePrice = 7.00,
            perKmPrice = 1.80,
            perMinPrice = 0.35,
            etaMinutes = 7,
            carExample = "Mercedes-Benz E-Class, BMW 5"
        ),
        Tariff(
            type = TariffType.MINIVAN,
            titleKa = "მინივენი",
            titleEn = "Minivan",
            titleRu = "Минивэн",
            basePrice = 6.00,
            perKmPrice = 1.40,
            perMinPrice = 0.25,
            etaMinutes = 8,
            carExample = "Mercedes Vito, Toyota Alphard"
        )
    )

    val defaultPaymentMethods = listOf(
        PaymentMethod("pm_1", PaymentType.BOG_IPAY, "Bank of Georgia (BOG iPay)", "4129", isDefault = true),
        PaymentMethod("pm_2", PaymentType.TBC_PAY, "TBC Bank Card", "8834", isDefault = false),
        PaymentMethod("pm_3", PaymentType.CASH, "ნაღდი ანგარიშსწორება", "", isDefault = false)
    )

    // Current State Holders
    private val _currentUser = MutableStateFlow(
        User(
            id = "usr_001",
            firstName = "დავით",
            lastName = "ბერიძე",
            phone = "+995 599 12 34 56",
            email = "davit.beridze@taxigo.ge",
            role = UserRole.PASSENGER,
            rating = 4.96,
            balance = 145.80,
            driverProfile = DriverProfile(
                isOnline = true,
                vehicle = DriverVehicle("Toyota", "Camry Hybrid", "შავი (Black)", "GA-555-TX", 2022),
                documents = listOf(
                    DriverDocument("პირადობის მოწმობა", "ID_CARD", VerificationStatus.APPROVED, "01024098765", "2030-05-15"),
                    DriverDocument("მართვის მოწმობა", "LICENSE", VerificationStatus.APPROVED, "DL-994821", "2029-11-20"),
                    DriverDocument("ტექ-პასპორტი", "TECH_PASSPORT", VerificationStatus.APPROVED, "TP-771120", "2028-04-10")
                )
            )
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.KA)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _activeRide = MutableStateFlow<Ride?>(null)
    val activeRide: StateFlow<Ride?> = _activeRide.asStateFlow()

    private val _rideHistory = MutableStateFlow(
        listOf(
            Ride(
                id = "ride_701",
                passengerName = "დავით ბერიძე",
                passengerPhone = "+995 599 12 34 56",
                passengerRating = 4.96,
                driverName = "გიორგი მამულაშვილი",
                driverPhone = "+995 577 88 99 00",
                driverRating = 4.94,
                vehicle = DriverVehicle("Toyota", "Prius IV", "თეთრი", "TX-101-GO", 2020),
                pickup = predefinedLocations[0],
                dropoff = predefinedLocations[1],
                tariff = TariffType.ECONOMY,
                distanceKm = 4.2,
                durationMin = 11,
                baseFare = 2.50,
                discount = 1.00,
                tip = 2.00,
                finalFare = 7.10,
                status = RideStatus.COMPLETED,
                paymentMethod = defaultPaymentMethods[0],
                userRating = 5,
                userReview = "ძალიან სუფთა მანქანა და თავაზიანი მძღოლი!",
                dateString = "გუშინ, 19:40"
            ),
            Ride(
                id = "ride_700",
                passengerName = "დავით ბერიძე",
                passengerPhone = "+995 599 12 34 56",
                passengerRating = 4.96,
                driverName = "ლევან კვარაცხელია",
                driverPhone = "+995 591 44 33 22",
                driverRating = 4.98,
                vehicle = DriverVehicle("Mercedes", "E-Class", "შავი", "EX-777-VIP", 2021),
                pickup = predefinedLocations[2],
                dropoff = predefinedLocations[4],
                tariff = TariffType.BUSINESS,
                distanceKm = 19.8,
                durationMin = 28,
                baseFare = 7.00,
                discount = 0.0,
                tip = 5.00,
                finalFare = 47.60,
                status = RideStatus.COMPLETED,
                paymentMethod = defaultPaymentMethods[0],
                userRating = 5,
                userReview = "იდეალური მომსახურება აეროპორტამდე",
                dateString = "3 დღის წინ, 08:15"
            )
        )
    )
    val rideHistory: StateFlow<List<Ride>> = _rideHistory.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            AppNotification("notif_1", "მოგესალმებათ Taxigo! 🚕", "მიიღეთ 20% ფასდაკლება პრომოკოდით TBILISI2026", "10 წთ წინ"),
            AppNotification("notif_2", "მძღოლის ანგარიში", "თქვენი დოკუმენტები წარმატებით დამოწმდა ადმინისტრაციის მიერ.", "1 სთ წინ"),
            AppNotification("notif_3", "გადახდა წარმატებულია", "ქვითარი #ride_701 თანხა 7.10₾ ჩამოიჭრა ბარათიდან", "გუშინ")
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("c1", "გიორგი (მძღოლი)", false, "გამარჯობა! 3 წუთში თქვენთან ვარ.", "14:31"),
            ChatMessage("c2", "დავით (მგზავრი)", true, "გამარჯობა, შლაგბაუმთან გელოდებით.", "14:32")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Driver applications for Admin review
    private val _driverApplications = MutableStateFlow(
        listOf(
            User(
                id = "drv_app_01",
                firstName = "ირაკლი",
                lastName = "ჩხეიძე",
                phone = "+995 555 71 82 93",
                email = "irakli.chkheidze@gmail.com",
                role = UserRole.DRIVER,
                rating = 5.0,
                balance = 50.0,
                driverProfile = DriverProfile(
                    isOnline = false,
                    vehicle = DriverVehicle("Hyundai", "Sonata", "ნაცრისფერი", "IR-456-CH", 2021),
                    documents = listOf(
                        DriverDocument("მართვის მოწმობა", "LICENSE", VerificationStatus.PENDING, "DL-881920", "2029-08-11"),
                        DriverDocument("ტექ-პასპორტი", "TECH_PASSPORT", VerificationStatus.PENDING, "TP-339911", "2027-12-01")
                    ),
                    verificationStatus = VerificationStatus.PENDING
                )
            ),
            User(
                id = "drv_app_02",
                firstName = "ნიკოლოზ",
                lastName = "გაგნიძე",
                phone = "+995 593 11 22 33",
                email = "niko.gagnidze@mail.ge",
                role = UserRole.DRIVER,
                rating = 4.88,
                balance = 30.0,
                driverProfile = DriverProfile(
                    isOnline = false,
                    vehicle = DriverVehicle("Skoda", "Superb", "შავი", "NK-999-TX", 2023),
                    documents = listOf(
                        DriverDocument("მართვის მოწმობა", "LICENSE", VerificationStatus.PENDING, "DL-552211", "2031-02-14"),
                        DriverDocument("ტექ-პასპორტი", "TECH_PASSPORT", VerificationStatus.PENDING, "TP-664411", "2028-09-09")
                    ),
                    verificationStatus = VerificationStatus.PENDING
                )
            )
        )
    )
    val driverApplications: StateFlow<List<User>> = _driverApplications.asStateFlow()

    // Simulation of driver online / offline
    private val _isDriverOnline = MutableStateFlow(true)
    val isDriverOnline: StateFlow<Boolean> = _isDriverOnline.asStateFlow()

    // Driver incoming order alert
    private val _driverIncomingOrder = MutableStateFlow<Ride?>(null)
    val driverIncomingOrder: StateFlow<Ride?> = _driverIncomingOrder.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun switchRole(role: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = role)
    }

    fun toggleDriverOnline() {
        val next = !_isDriverOnline.value
        _isDriverOnline.value = next
        val profile = _currentUser.value.driverProfile?.copy(isOnline = next)
        _currentUser.value = _currentUser.value.copy(driverProfile = profile)
    }

    fun startRide(ride: Ride) {
        _activeRide.value = ride
    }

    fun updateRideStatus(newStatus: RideStatus) {
        val current = _activeRide.value ?: return
        if (newStatus == RideStatus.COMPLETED) {
            val finished = current.copy(status = RideStatus.COMPLETED)
            _activeRide.value = finished
            _rideHistory.value = listOf(finished) + _rideHistory.value
        } else if (newStatus == RideStatus.CANCELLED) {
            _activeRide.value = null
        } else {
            _activeRide.value = current.copy(status = newStatus)
        }
    }

    fun rateAndCloseRide(rating: Int, tip: Double, review: String) {
        val current = _activeRide.value ?: return
        val updated = current.copy(userRating = rating, tip = tip, userReview = review)
        _rideHistory.value = _rideHistory.value.map { if (it.id == current.id) updated else it }
        _activeRide.value = null
    }

    fun sendChatMessage(text: String, isPassenger: Boolean) {
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = if (isPassenger) _currentUser.value.firstName else "მძღოლი",
            isFromPassenger = isPassenger,
            text = text,
            time = "14:${(30..59).random()}"
        )
        _chatMessages.value = _chatMessages.value + newMsg
    }

    fun approveDriver(driverId: String) {
        _driverApplications.value = _driverApplications.value.map { driver ->
            if (driver.id == driverId) {
                driver.copy(
                    driverProfile = driver.driverProfile?.copy(
                        verificationStatus = VerificationStatus.APPROVED,
                        documents = driver.driverProfile.documents.map { it.copy(status = VerificationStatus.APPROVED) }
                    )
                )
            } else driver
        }
    }

    fun rejectDriver(driverId: String) {
        _driverApplications.value = _driverApplications.value.map { driver ->
            if (driver.id == driverId) {
                driver.copy(
                    driverProfile = driver.driverProfile?.copy(
                        verificationStatus = VerificationStatus.REJECTED,
                        documents = driver.driverProfile.documents.map { it.copy(status = VerificationStatus.REJECTED) }
                    )
                )
            } else driver
        }
    }

    fun triggerSimulatedDriverOrder() {
        val sampleOrder = Ride(
            id = "ord_${System.currentTimeMillis()}",
            passengerName = "სალომე ჯაფარიძე",
            passengerPhone = "+995 595 12 88 99",
            passengerRating = 4.98,
            driverName = _currentUser.value.firstName + " " + _currentUser.value.lastName,
            driverPhone = _currentUser.value.phone,
            driverRating = 4.92,
            vehicle = _currentUser.value.driverProfile?.vehicle ?: DriverVehicle("Toyota", "Camry", "შავი", "GA-555-TX", 2022),
            pickup = predefinedLocations[1], // Freedom Square
            dropoff = predefinedLocations[2], // Vake Park
            tariff = TariffType.COMFORT,
            distanceKm = 5.4,
            durationMin = 14,
            baseFare = 4.00,
            discount = 0.0,
            tip = 0.0,
            finalFare = 11.20,
            status = RideStatus.SEARCHING,
            paymentMethod = defaultPaymentMethods[0],
            dateString = "ახლა"
        )
        _driverIncomingOrder.value = sampleOrder
    }

    fun acceptIncomingDriverOrder() {
        val order = _driverIncomingOrder.value ?: return
        _activeRide.value = order.copy(status = RideStatus.DRIVER_ACCEPTED)
        _driverIncomingOrder.value = null
    }

    fun declineIncomingDriverOrder() {
        _driverIncomingOrder.value = null
    }
}
