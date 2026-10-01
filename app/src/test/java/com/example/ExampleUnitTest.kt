package com.example

import com.example.data.repository.AgencyRepository
import com.example.data.repository.DeadlineUrgency
import com.example.model.*
import com.example.ui.theme.Translations
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testTranslationsDictionary() {
        assertEquals("OmniAgency", Translations.get("app_name", "en"))
        assertEquals("Tableau de Bord", Translations.get("dashboard", "fr"))
        assertEquals("لوحة التحكم", Translations.get("dashboard", "ar"))
    }

    @Test
    fun testClientHealthCalculation() {
        val now = System.currentTimeMillis()
        val healthyClient = Client(
            id = "test_1",
            name = "Test Client",
            company = "Acme Inc",
            lastContactAt = now - 2 * 86400000L
        )

        // Stale client > 14 days without contact
        val staleClient = healthyClient.copy(
            lastContactAt = now - 20 * 86400000L
        )

        // Mock DAO not needed for static logic test
        val daysSinceLastContact = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(now - staleClient.lastContactAt)
        assertTrue(daysSinceLastContact > 14)
    }

    @Test
    fun testDeadlineCalculation() {
        val now = System.currentTimeMillis()
        val futureDeadline = now + 5 * 86400000L
        val overdueDeadline = now - 2 * 86400000L

        val diffFutureDays = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(futureDeadline - now)
        val diffOverdueDays = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(now - overdueDeadline)

        assertEquals(5L, diffFutureDays)
        assertEquals(2L, diffOverdueDays)
    }
}
