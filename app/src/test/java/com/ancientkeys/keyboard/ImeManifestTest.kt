package com.ancientkeys.keyboard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ImeManifestTest {

    @Test
    fun testManifestRegistrationAndSecurity() {
        val manifestFile = File("src/main/AndroidManifest.xml")
        assertTrue("AndroidManifest.xml must exist", manifestFile.exists())
        val content = manifestFile.readText()

        // Verify service registration
        assertTrue("Manifest must declare AncientKeysIME service", content.contains("AncientKeysIME"))
        assertTrue("Service must require BIND_INPUT_METHOD permission", content.contains("android.permission.BIND_INPUT_METHOD"))
        assertTrue("Service must handle android.view.InputMethod intent", content.contains("android.view.InputMethod"))
        assertTrue("Service must link to @xml/method", content.contains("@xml/method"))

        // Security check: Verify no internet or dangerous permissions
        assertTrue("Manifest must have android:allowBackup=\"false\"", content.contains("android:allowBackup=\"false\""))
        assertFalse("Manifest must NOT declare INTERNET permission", content.contains("android.permission.INTERNET"))
        assertFalse("Manifest must NOT declare RECORD_AUDIO", content.contains("android.permission.RECORD_AUDIO"))
        assertFalse("Manifest must NOT declare READ_EXTERNAL_STORAGE", content.contains("android.permission.READ_EXTERNAL_STORAGE"))
    }

    @Test
    fun testMethodXmlConfiguration() {
        val methodFile = File("src/main/res/xml/method.xml")
        assertTrue("method.xml must exist", methodFile.exists())
        val content = methodFile.readText()

        assertTrue("Must be an input-method element", content.contains("<input-method"))
        assertTrue("Must be marked isDefault=false", content.contains("android:isDefault=\"false\""))
        assertTrue("Must declare a subtype", content.contains("<subtype"))
    }
}

