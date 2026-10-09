package com.example

import com.example.data.AppLanguage
import com.example.data.Localization
import com.example.data.MockDatabase
import com.example.data.Ride
import com.example.data.RideStatus
import com.example.data.TariffType
import com.example.data.UserRole
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testLocalizationDefaults() {
        val appTitle = Localization.get("app_title", AppLanguage.KA)
        assertEquals("Taxigo", appTitle)

        val loginKa = Localization.get("login", AppLanguage.KA)
        assertEquals("შესვლა", loginKa)

        val loginEn = Localization.get("login", AppLanguage.EN)
        assertEquals("Log In", loginEn)
    }

    @Test
    fun testTariffCalculation() {
        val economy = MockDatabase.defaultTariffs.first { it.type == TariffType.ECONOMY }
        val distance = 5.0
        val duration = 10.0
        val expected = economy.basePrice + (distance * economy.perKmPrice) + (duration * economy.perMinPrice)
        // base = 2.50, 5 * 0.8 = 4.00, 10 * 0.15 = 1.50 -> sum = 8.00
        assertEquals(8.00, expected, 0.01)
    }

    @Test
    fun testDriverRoleSwitching() {
        MockDatabase.switchRole(UserRole.PASSENGER)
        assertEquals(UserRole.PASSENGER, MockDatabase.currentUser.value.role)

        MockDatabase.switchRole(UserRole.DRIVER)
        assertEquals(UserRole.DRIVER, MockDatabase.currentUser.value.role)
    }

    @Test
    fun testRideLifecycle() {
        val sampleRide = Ride(
            id = "test_ride_1",
            passengerName = "ტესტ მგზავრი",
            passengerPhone = "+995 555 11 22 33",
            passengerRating = 5.0,
            driverName = "ტესტ მძღოლი",
            driverPhone = "+995 577 11 22 33",
            driverRating = 4.9,
            vehicle = MockDatabase.currentUser.value.driverProfile!!.vehicle,
            pickup = MockDatabase.predefinedLocations[0],
            dropoff = MockDatabase.predefinedLocations[1],
            tariff = TariffType.ECONOMY,
            distanceKm = 4.0,
            durationMin = 10,
            baseFare = 2.50,
            finalFare = 7.20,
            status = RideStatus.SEARCHING,
            paymentMethod = MockDatabase.defaultPaymentMethods[0]
        )

        MockDatabase.startRide(sampleRide)
        assertEquals(RideStatus.SEARCHING, MockDatabase.activeRide.value?.status)

        MockDatabase.updateRideStatus(RideStatus.DRIVER_ACCEPTED)
        assertEquals(RideStatus.DRIVER_ACCEPTED, MockDatabase.activeRide.value?.status)

        MockDatabase.updateRideStatus(RideStatus.IN_PROGRESS)
        assertEquals(RideStatus.IN_PROGRESS, MockDatabase.activeRide.value?.status)

        MockDatabase.updateRideStatus(RideStatus.COMPLETED)
        assertEquals(RideStatus.COMPLETED, MockDatabase.activeRide.value?.status)

        MockDatabase.rateAndCloseRide(5, 2.0, "ძალიან კარგი")
        assertNull(MockDatabase.activeRide.value)
    }
}
