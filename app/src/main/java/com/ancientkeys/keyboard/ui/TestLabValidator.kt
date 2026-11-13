package com.ancientkeys.keyboard.ui

import android.text.InputType
import android.view.inputmethod.EditorInfo
import com.ancientkeys.keyboard.ime.ImeInputHelper
import com.ancientkeys.keyboard.ime.romanization.BrahmiRomanizer
import com.ancientkeys.keyboard.ime.romanization.GreekRomanizer
import com.ancientkeys.keyboard.ime.romanization.KharosthiRomanizer
import com.ancientkeys.keyboard.model.ScriptRegistry

data class TestLabCheckResult(
    val name: String,
    val passed: Boolean,
    val details: String,
    val actionableResolution: String = ""
)

/**
 * Automatable verification engine for the in-app IME Test Lab.
 *
 * Provides programmatic assertions and generates actionable diagnostics
 * for both automated unit tests and user-facing UI diagnostics.
 */
object TestLabValidator {

    /**
     * Executes a full self-diagnostic audit of keyboard behavior against synthetic fields.
     */
    fun runFullAudit(): List<TestLabCheckResult> {
        val results = mutableListOf<TestLabCheckResult>()

        // Check 1: Sensitive field detection for standard password
        val passwordInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val passShield = ImeInputHelper.isSensitiveField(passwordInfo)
        results.add(
            TestLabCheckResult(
                name = "Password Shield Detection",
                passed = passShield,
                details = if (passShield) "Standard password field correctly flagged as sensitive"
                else "FAILURE: Password field was not identified as sensitive",
                actionableResolution = if (passShield) "" else "Review ImeInputHelper.isSensitiveField variation masks"
            )
        )

        // Check 2: Sensitive field detection for numeric PIN
        val pinInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        val pinShield = ImeInputHelper.isSensitiveField(pinInfo)
        results.add(
            TestLabCheckResult(
                name = "Numeric PIN Shield Detection",
                passed = pinShield,
                details = if (pinShield) "Numeric PIN field correctly flagged as sensitive"
                else "FAILURE: Numeric PIN was not identified as sensitive",
                actionableResolution = if (pinShield) "" else "Check TYPE_CLASS_NUMBER password variation handling"
            )
        )

        // Check 3: Normal field must NOT trigger shield
        val normalInfo = EditorInfo().apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        }
        val normalNotShield = !ImeInputHelper.isSensitiveField(normalInfo)
        results.add(
            TestLabCheckResult(
                name = "Normal Field Access",
                passed = normalNotShield,
                details = if (normalNotShield) "Normal text field allows historical input"
                else "FAILURE: Normal field erroneously flagged as sensitive",
                actionableResolution = if (normalNotShield) "" else "Ensure normal text fields return false from sensitive check"
            )
        )

        // Check 4: Script registry completeness
        val scripts = ScriptRegistry.allScripts
        val allScriptsValid = scripts.size == 6 && scripts.all { it.keys.isNotEmpty() }
        results.add(
            TestLabCheckResult(
                name = "Script Registry Completeness",
                passed = allScriptsValid,
                details = if (allScriptsValid) "All 6 historical scripts populated with valid keys"
                else "FAILURE: Scripts missing or have empty key inventories",
                actionableResolution = if (allScriptsValid) "" else "Verify ScriptRegistry initialization"
            )
        )

        // Check 5: Brahmi Romanizer verification
        val asokaTranslit = BrahmiRomanizer.transliterateToken("asoka")
        val romanizerValid = asokaTranslit == "𑀅𑀲\uD804\uDC44𑀓"
        results.add(
            TestLabCheckResult(
                name = "Brahmi Romanized Input",
                passed = romanizerValid,
                details = if (romanizerValid) "'asoka' transliterates accurately to '𑀅𑀲\uD804\uDC44𑀓'"
                else "FAILURE: Romanizer output was '$asokaTranslit'",
                actionableResolution = if (romanizerValid) "" else "Check BrahmiRomanizer consonant & vowel sign composition logic"
            )
        )

        // Check 6: Kharosthi Romanizer verification
        val kharosthiTranslit = KharosthiRomanizer.transliterateToken("dharma")
        val kharosthiValid = kharosthiTranslit == "𐨢𐨪𐨨"
        results.add(
            TestLabCheckResult(
                name = "Kharosthi Romanized Input",
                passed = kharosthiValid,
                details = if (kharosthiValid) "'dharma' transliterates accurately to '𐨢𐨪𐨨'"
                else "FAILURE: Kharosthi transliteration output was '$kharosthiTranslit'",
                actionableResolution = if (kharosthiValid) "" else "Check KharosthiRomanizer consonant mapping"
            )
        )

        // Check 7: Greek Romanizer verification (with final sigma)
        val greekTranslit = GreekRomanizer.transliterateToken("logos")
        val greekValid = greekTranslit == "λογος"
        results.add(
            TestLabCheckResult(
                name = "Ancient Greek Romanized Input",
                passed = greekValid,
                details = if (greekValid) "'logos' transliterates accurately to 'λογος' (with final sigma)"
                else "FAILURE: Greek transliteration output was '$greekTranslit'",
                actionableResolution = if (greekValid) "" else "Check GreekRomanizer transliteration and final sigma rules"
            )
        )

        // Check 8: Brahmi Easy Phonetic verification
        val brahmiPhonetic = com.ancientkeys.keyboard.ime.phonetic.BrahmiEasyPhoneticEngine.convert("asoka")
        val brahmiPhoneticValid = brahmiPhonetic == "𑀅𑀰𑁄𑀓"
        results.add(
            TestLabCheckResult(
                name = "Brahmi Easy Phonetic Input",
                passed = brahmiPhoneticValid,
                details = if (brahmiPhoneticValid) "'asoka' converts phonetically to '𑀅𑀰𑁄𑀓'"
                else "FAILURE: Brahmi Easy Phonetic output was '$brahmiPhonetic'",
                actionableResolution = if (brahmiPhoneticValid) "" else "Check BrahmiEasyPhoneticEngine mapping"
            )
        )

        // Check 9: Kharosthi Easy Phonetic verification
        val kharosthiPhonetic = com.ancientkeys.keyboard.ime.phonetic.KharosthiEasyPhoneticEngine.convert("za")
        val kharosthiPhoneticValid = kharosthiPhonetic == "𐨰"
        results.add(
            TestLabCheckResult(
                name = "Kharosthi Easy Phonetic Input",
                passed = kharosthiPhoneticValid,
                details = if (kharosthiPhoneticValid) "'za' converts to Iranian loan '𐨰'"
                else "FAILURE: Kharosthi Easy Phonetic output was '$kharosthiPhonetic'",
                actionableResolution = if (kharosthiPhoneticValid) "" else "Check KharosthiEasyPhoneticEngine za mapping"
            )
        )

        // Check 10: Greek Easy Phonetic verification
        val greekPhonetic = com.ancientkeys.keyboard.ime.phonetic.GreekEasyPhoneticEngine.convert("th")
        val greekPhoneticValid = greekPhonetic == "θ"
        results.add(
            TestLabCheckResult(
                name = "Greek Easy Phonetic Input",
                passed = greekPhoneticValid,
                details = if (greekPhoneticValid) "'th' converts to 'θ'"
                else "FAILURE: Greek Easy Phonetic output was '$greekPhonetic'",
                actionableResolution = if (greekPhoneticValid) "" else "Check GreekEasyPhoneticEngine digraph mapping"
            )
        )

        return results
    }

    /**
     * Formats results into an actionable diagnostic summary string.
     */
    fun formatAuditSummary(results: List<TestLabCheckResult>): String {
        val total = results.size
        val passed = results.count { it.passed }
        val failed = total - passed

        val sb = StringBuilder()
        sb.appendLine("=== TEST LAB AUDIT REPORT: $passed/$total CHECKS PASSED ===")
        for (r in results) {
            val status = if (r.passed) "[PASS]" else "[FAIL]"
            sb.appendLine("$status ${r.name}: ${r.details}")
            if (!r.passed && r.actionableResolution.isNotBlank()) {
                sb.appendLine("       -> FIX: ${r.actionableResolution}")
            }
        }
        return sb.toString()
    }
}
