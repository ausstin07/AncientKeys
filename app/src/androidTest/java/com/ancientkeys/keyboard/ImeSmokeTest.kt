package com.ancientkeys.keyboard

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ancientkeys.keyboard.ime.AncientKeysIME
import com.ancientkeys.keyboard.model.ScriptRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android instrumentation smoke test verifying service startup, context binding,
 * and script registry availability in an Android runtime environment.
 */
@RunWith(AndroidJUnit4::class)
class ImeSmokeTest {

    @Test
    fun testAppContextAndScriptRegistry() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("com.ancientkeys.keyboard", appContext.packageName)

        // Verify all 6 historical scripts load in device environment
        val scripts = ScriptRegistry.allScripts
        assertEquals(6, scripts.size)
    }

    @Test
    fun testImeServiceClassInstantiation() {
        // Verify AncientKeysIME can be referenced and loaded by classloader
        val clazz = Class.forName("com.ancientkeys.keyboard.ime.AncientKeysIME")
        assertNotNull(clazz)
        assertEquals(AncientKeysIME::class.java, clazz)
    }
}

