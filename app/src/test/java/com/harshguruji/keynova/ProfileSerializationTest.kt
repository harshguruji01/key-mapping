package com.harshguruji.keynova

import com.harshguruji.keynova.data.repository.DefaultProfiles
import com.harshguruji.keynova.utils.ProfileExporter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileSerializationTest {

    @Test
    fun testExportAndImportRoundTrip() {
        val fpsProfile = DefaultProfiles.createFpsProfile()

        // Export to JSON
        val json = ProfileExporter.exportToJson(fpsProfile)
        assertNotNull(json)
        assertTrue(json.contains("FPS Gaming Pro"))
        assertTrue(json.contains("WASD Movement"))

        // Import back from JSON
        val importResult = ProfileExporter.importFromJson(json)
        assertTrue(importResult.isSuccess)

        val importedProfile = importResult.getOrThrow()
        assertEquals(fpsProfile.name, importedProfile.name)
        assertEquals(fpsProfile.mappings.size, importedProfile.mappings.size)
        assertEquals(fpsProfile.controls.size, importedProfile.controls.size)
    }

    @Test
    fun testDefaultProfilesIntegrity() {
        val fps = DefaultProfiles.createFpsProfile()
        val moba = DefaultProfiles.createMobaProfile()
        val retro = DefaultProfiles.createRetroGamepadProfile()

        assertTrue(fps.mappings.isNotEmpty())
        assertTrue(fps.controls.isNotEmpty())

        assertTrue(moba.mappings.isNotEmpty())
        assertTrue(moba.controls.isNotEmpty())

        assertTrue(retro.mappings.isNotEmpty())
        assertTrue(retro.controls.isNotEmpty())
    }
}
