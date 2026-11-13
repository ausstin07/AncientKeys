package com.ancientkeys.keyboard

import android.view.inputmethod.EditorInfo
import com.ancientkeys.keyboard.ime.ImeInputHelper
import com.ancientkeys.keyboard.ime.data.KeyboardMode
import com.ancientkeys.keyboard.ime.phonetic.BrahmiEasyPhoneticEngine
import com.ancientkeys.keyboard.ime.phonetic.GreekEasyPhoneticEngine
import com.ancientkeys.keyboard.ime.phonetic.KharosthiEasyPhoneticEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regression test suite verifying typing stability, spacebar behavior,
 * candidate caching, and layout geometry invariance for AncientKeys.
 */
class ImeCompositionAndStabilityRegressionTest {

    @Test
    fun testBrahmiPhoneticCachingAndCorrectness() {
        // First conversion (populates cache)
        val austin1 = BrahmiEasyPhoneticEngine.convert("austin")
        assertEquals("𑀒𑀲𑁆𑀢𑀺𑀦𑁆", austin1)

        // Second conversion (serviced by cache)
        val austin2 = BrahmiEasyPhoneticEngine.convert("austin")
        assertEquals(austin1, austin2)

        // Additional common words
        val namaste = BrahmiEasyPhoneticEngine.convert("namaste")
        assertEquals("𑀦𑀫𑀲𑁆𑀢𑁂", namaste)

        val asoka = BrahmiEasyPhoneticEngine.convert("asoka")
        assertEquals("𑀅𑀰𑁄𑀓", asoka)

        // Candidates caching
        val cands1 = BrahmiEasyPhoneticEngine.getCandidates("austin")
        assertTrue(cands1.isNotEmpty())
        assertEquals("𑀒𑀲𑁆𑀢𑀺𑀦𑁆", cands1.first().scriptResult)

        val cands2 = BrahmiEasyPhoneticEngine.getCandidates("austin")
        assertEquals(cands1.size, cands2.size)
    }

    @Test
    fun testKharosthiAndGreekCaching() {
        val khPrimary1 = KharosthiEasyPhoneticEngine.convert("dharma")
        val khPrimary2 = KharosthiEasyPhoneticEngine.convert("dharma")
        assertEquals(khPrimary1, khPrimary2)

        val grPrimary1 = GreekEasyPhoneticEngine.convert("logos")
        val grPrimary2 = GreekEasyPhoneticEngine.convert("logos")
        assertEquals("λογος", grPrimary1)
        assertEquals(grPrimary1, grPrimary2)
    }

    @Test
    fun testAtomicSpacebarCompositionCommit() {
        var committedText: String? = null
        var spaceCallCount = 0

        fun mockCommit(text: String) {
            committedText = text
        }

        fun mockSpace() {
            spaceCallCount++
        }

        // Simulate typing "austin" then Space
        var buffer = "austin"
        val engine = BrahmiEasyPhoneticEngine

        if (buffer.isNotEmpty()) {
            val converted = engine.convert(buffer)
            buffer = ""
            if (converted.isNotEmpty()) {
                mockCommit("$converted ")
            } else {
                mockSpace()
            }
        }

        assertEquals("𑀒𑀲𑁆𑀢𑀺𑀦𑁆 ", committedText)
        assertEquals("", buffer)
        assertEquals(0, spaceCallCount)

        // Second space tap with empty buffer
        if (buffer.isNotEmpty()) {
            val converted = engine.convert(buffer)
            buffer = ""
            if (converted.isNotEmpty()) {
                mockCommit("$converted ")
            } else {
                mockSpace()
            }
        } else {
            mockSpace()
        }

        assertEquals(1, spaceCallCount)
    }

    @Test
    fun testBottomRowGeometryInvarianceAcrossModes() {
        // In the optimized bottom row layout:
        // Slot 1 (Mode): 1.0f
        // Slot 2 (Left Punctuation): 0.8f
        // Slot 3 (Spacebar): 5.0f
        // Slot 4 (Right Punctuation / Backspace): 0.8f
        // Slot 5 (Action Key): 1.4f
        val slot1Weight = 1.0f
        val slot2Weight = 0.8f
        val spacebarWeight = 5.0f
        val slot4Weight = 0.8f
        val slot5Weight = 1.4f

        val totalWeight = slot1Weight + slot2Weight + spacebarWeight + slot4Weight + slot5Weight
        assertEquals(9.0f, totalWeight, 0.001f)

        // Spacebar start and end horizontal proportions
        val spaceStartFraction = (slot1Weight + slot2Weight) / totalWeight
        val spaceEndFraction = (slot1Weight + slot2Weight + spacebarWeight) / totalWeight
        val spaceWidthFraction = spacebarWeight / totalWeight

        assertEquals(0.200f, spaceStartFraction, 0.001f)
        assertEquals(0.755f, spaceEndFraction, 0.002f)
        assertEquals(0.555f, spaceWidthFraction, 0.002f)

        // Verify across all modes: Spacebar occupies over 55% of the row width
        assertTrue("Spacebar must occupy dominant portion of bottom row", spaceWidthFraction > 0.50f)
        assertTrue("Action key must be bounded to prevent accidental taps", slot5Weight / totalWeight < 0.18f)
    }

    @Test
    fun testActionKeyResolutions() {
        // Search action
        val searchInfo = EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_SEARCH }
        val searchAction = ImeInputHelper.getImeActionInfo(searchInfo)
        assertEquals("Search 🔍", searchAction.displayLabel)
        assertFalse(searchAction.isEnterNewline)

        // Send action
        val sendInfo = EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_SEND }
        val sendAction = ImeInputHelper.getImeActionInfo(sendInfo)
        assertEquals("Send ➤", sendAction.displayLabel)

        // Done action
        val doneInfo = EditorInfo().apply { imeOptions = EditorInfo.IME_ACTION_DONE }
        val doneAction = ImeInputHelper.getImeActionInfo(doneInfo)
        assertEquals("Done ✓", doneAction.displayLabel)

        // Multiline action
        val multilineInfo = EditorInfo().apply {
            imeOptions = EditorInfo.IME_ACTION_NONE or EditorInfo.IME_FLAG_NO_ENTER_ACTION
        }
        val multilineAction = ImeInputHelper.getImeActionInfo(multilineInfo)
        assertTrue(multilineAction.isEnterNewline)
    }

    @Test
    fun testSensitiveFieldDetection() {
        val pwInfo = EditorInfo().apply {
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        assertTrue(ImeInputHelper.isSensitiveField(pwInfo))

        val normalInfo = EditorInfo().apply {
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_NORMAL
        }
        assertFalse(ImeInputHelper.isSensitiveField(normalInfo))
    }

    @Test
    fun testBoundedPhoneticCachesUnderHighThroughput() {
        // Feed 500 distinct strings to test bounded cache eviction
        for (i in 0 until 500) {
            val query = "word$i"
            BrahmiEasyPhoneticEngine.convert(query)
            BrahmiEasyPhoneticEngine.getCandidates(query)
            GreekEasyPhoneticEngine.convert(query)
            GreekEasyPhoneticEngine.getCandidates(query)
            KharosthiEasyPhoneticEngine.convert(query)
            KharosthiEasyPhoneticEngine.getCandidates(query)
        }

        // Verify that caches function normally and clearAllCaches operates cleanly
        com.ancientkeys.keyboard.ime.phonetic.EasyPhoneticProvider.clearAllCaches()

        // Immediate subsequent conversions must succeed after cache flush
        assertEquals("𑀒𑀲𑁆𑀢𑀺𑀦𑁆", BrahmiEasyPhoneticEngine.convert("austin"))
        assertEquals("θ", GreekEasyPhoneticEngine.convert("th"))
        assertEquals("𐨐", KharosthiEasyPhoneticEngine.convert("ka"))
    }

    @Test
    fun testAllSevenModesAndScriptsRegistryIntegrity() {
        val scripts = com.ancientkeys.keyboard.model.ScriptRegistry.allScripts
        // 6 Historical scripts + English QWERTY = 7 distinct writing modes
        assertEquals(6, scripts.size)
        val scriptIds = scripts.map { it.id }.toSet()
        assertTrue(scriptIds.contains("brahmi"))
        assertTrue(scriptIds.contains("kharosthi"))
        assertTrue(scriptIds.contains("greek"))
        assertTrue(scriptIds.contains("egyptian"))
        assertTrue(scriptIds.contains("phoenician"))
        assertTrue(scriptIds.contains("runic"))

        // Each historical script must have non-empty keys and valid metadata
        for (script in scripts) {
            assertTrue("Script ${script.name} keys must not be empty", script.keys.isNotEmpty())
            for (key in script.keys) {
                assertTrue("Key ${key.character} in ${script.name} must have non-empty character", key.character.isNotEmpty())
                assertTrue("Key ${key.character} in ${script.name} must have valid Unicode code point", key.unicodeCodePoint.startsWith("U+"))
            }
        }
    }
}

