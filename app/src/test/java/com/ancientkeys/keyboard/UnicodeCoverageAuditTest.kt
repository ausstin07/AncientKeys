package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.ime.romanization.ScriptRomanizerProvider
import com.ancientkeys.keyboard.model.ScriptRegistry
import com.ancientkeys.keyboard.model.UnicodeCoverageAudit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UnicodeCoverageAuditTest {

    @Test
    fun testAuditReturnsAllSixHistoricalScripts() {
        val auditList = UnicodeCoverageAudit.getFullCoverageAudit()
        assertEquals("Should return coverage reports for exactly 6 scripts", 6, auditList.size)

        val scriptIds = auditList.map { it.scriptId }.toSet()
        val expectedIds = setOf("brahmi", "kharosthi", "greek", "phoenician", "runic", "egyptian")
        assertEquals(expectedIds, scriptIds)
    }

    @Test
    fun testPhoenicianCompleteUnicodeBlockCoverage() {
        val auditList = UnicodeCoverageAudit.getFullCoverageAudit()
        val phoenicianReport = auditList.first { it.scriptId == "phoenician" }

        assertEquals(29, phoenicianReport.totalUnicodeAllocated)
        assertEquals(29, phoenicianReport.ancientKeysSupportedCount)
        assertTrue(phoenicianReport.omittedOrExtendedNotes.contains("100% coverage"))
    }

    @Test
    fun testBrahmiCoverageIncludesNumeralsAndVowels() {
        val auditList = UnicodeCoverageAudit.getFullCoverageAudit()
        val brahmiReport = auditList.first { it.scriptId == "brahmi" }

        assertEquals(109, brahmiReport.totalUnicodeAllocated)
        assertEquals(91, brahmiReport.ancientKeysSupportedCount)
        assertTrue(brahmiReport.coverageCategoryDescription.contains("Numerals"))
        assertTrue(brahmiReport.coverageCategoryDescription.contains("Decimal Digits"))
    }

    @Test
    fun testKharosthiCoverageIncludesZaAndNumerals() {
        val auditList = UnicodeCoverageAudit.getFullCoverageAudit()
        val kharosthiReport = auditList.first { it.scriptId == "kharosthi" }

        assertEquals(68, kharosthiReport.totalUnicodeAllocated)
        assertEquals(54, kharosthiReport.ancientKeysSupportedCount)
        assertTrue(kharosthiReport.coverageCategoryDescription.contains("ZA"))
        assertTrue(kharosthiReport.coverageCategoryDescription.contains("Gandhāran Numerals"))
    }

    @Test
    fun testReportsHaveValidDataIntegrity() {
        val auditList = UnicodeCoverageAudit.getFullCoverageAudit()
        for (report in auditList) {
            assertTrue("Script ID should not be blank", report.scriptId.isNotBlank())
            assertTrue("Script Name should not be blank", report.scriptName.isNotBlank())
            assertTrue("Unicode block should not be blank", report.unicodeBlock.isNotBlank())
            assertTrue("Unicode range should match pattern", report.unicodeBlockRange.startsWith("U+"))
            assertTrue("Total allocated should be positive", report.totalUnicodeAllocated > 0)
            assertTrue("Supported count should be positive", report.ancientKeysSupportedCount > 0)
            assertTrue("Supported count should not exceed total allocated", 
                report.ancientKeysSupportedCount <= report.totalUnicodeAllocated)
            assertTrue("Category description should not be blank", report.coverageCategoryDescription.isNotBlank())
            assertTrue("Omitted/Extended notes should not be blank", report.omittedOrExtendedNotes.isNotBlank())
        }
    }

    @Test
    fun testScriptRomanizerProviderSupportsOnlyDocumentedScripts() {
        val supported = ScriptRomanizerProvider.getAllSupportedScriptIds()
        assertEquals(listOf("brahmi", "kharosthi", "greek"), supported)

        assertTrue(ScriptRomanizerProvider.isRomanizationSupported("brahmi"))
        assertTrue(ScriptRomanizerProvider.isRomanizationSupported("kharosthi"))
        assertTrue(ScriptRomanizerProvider.isRomanizationSupported("greek"))
        assertTrue(ScriptRomanizerProvider.isRomanizationSupported("BRAHMI"))
        assertTrue(ScriptRomanizerProvider.isRomanizationSupported("Kharosthi"))

        assertNotNull(ScriptRomanizerProvider.getRomanizer("brahmi"))
        assertNotNull(ScriptRomanizerProvider.getRomanizer("kharosthi"))
        assertNotNull(ScriptRomanizerProvider.getRomanizer("greek"))

        // Strictly verify that scripts without standardized romanizers return false and null
        assertFalse(ScriptRomanizerProvider.isRomanizationSupported("egyptian"))
        assertFalse(ScriptRomanizerProvider.isRomanizationSupported("phoenician"))
        assertFalse(ScriptRomanizerProvider.isRomanizationSupported("runic"))
        assertFalse(ScriptRomanizerProvider.isRomanizationSupported("english"))
        assertFalse(ScriptRomanizerProvider.isRomanizationSupported("unknown"))

        assertNull(ScriptRomanizerProvider.getRomanizer("egyptian"))
        assertNull(ScriptRomanizerProvider.getRomanizer("phoenician"))
        assertNull(ScriptRomanizerProvider.getRomanizer("runic"))
        assertNull(ScriptRomanizerProvider.getRomanizer("unknown"))
    }
}

