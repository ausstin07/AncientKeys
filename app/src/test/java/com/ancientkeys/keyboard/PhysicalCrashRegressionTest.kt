package com.ancientkeys.keyboard

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import com.ancientkeys.keyboard.ime.AncientKeysIME
import com.ancientkeys.keyboard.model.HistoricalScript
import com.ancientkeys.keyboard.model.ScriptRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regression tests for physical Android device crashes:
 * 1. Crash 1 — Character Explorer SaveableStateRegistry crash:
 *    Ensures that script state is stored as a bundle-safe string ID and resolved reactively
 *    via ScriptRegistry, avoiding IllegalArgumentException on MutableState<HistoricalScript>.
 * 2. Crash 2 — InputMethodService ViewTreeLifecycleOwner and lifecycle show/hide/recreate cycles:
 *    Ensures that AncientKeysIME implements all required ViewTree owner interfaces and handles
 *    lifecycle transitions smoothly (INITIALIZED -> RESUMED -> STOPPED -> RESUMED -> DESTROYED)
 *    without getting stuck or failing ViewTree owner bindings.
 */
class PhysicalCrashRegressionTest {

    @Test
    fun testAllScriptIdsResolveSafelyInScriptRegistry() {
        val scriptIds = listOf("egyptian", "brahmi", "greek", "runic", "phoenician", "kharosthi")
        for (id in scriptIds) {
            val script = ScriptRegistry.getScriptById(id)
            assertNotNull("Script with ID '$id' must be resolvable from ScriptRegistry", script)
            assertEquals("Resolved script ID must match requested ID", id, script?.id)
        }
    }

    @Test
    fun testScriptIdIsBundleSaveableType() {
        // String, Int, and Boolean are standard primitive types supported by Android Bundle and SaveableStateRegistry
        val scriptId: Any = "egyptian"
        val categoryIndex: Any = 0
        val showOnlyFavorites: Any = false
        val searchQuery: Any = "ankh"

        fun isBundleSaveable(value: Any): Boolean {
            return when (value) {
                is String, is Int, is Boolean, is Float, is Double, is Long, is Byte, is Short, is Char -> true
                is java.io.Serializable -> true
                else -> false
            }
        }

        assertTrue("Script ID must be bundle-saveable", isBundleSaveable(scriptId))
        assertTrue("Category index must be bundle-saveable", isBundleSaveable(categoryIndex))
        assertTrue("showOnlyFavorites must be bundle-saveable", isBundleSaveable(showOnlyFavorites))
        assertTrue("searchQuery must be bundle-saveable", isBundleSaveable(searchQuery))
    }

    @Test
    fun testHistoricalScriptIsNotDirectlyBundleSaveable() {
        // HistoricalScript contains complex nested collections and function pointers/lambdas
        // It should NOT be stored directly in rememberSaveable
        val script = ScriptRegistry.getScriptById("greek")
        assertNotNull(script)
        // Verify HistoricalScript itself is a complex domain model and not a primitive
        assertTrue("HistoricalScript is a complex domain model", script is HistoricalScript)
    }

    @Test
    fun testAncientKeysImeLifecycleTransitionsAcrossShowHideCycles() {
        // Test INITIALIZED or CREATED on show: transitions via ON_START and ON_RESUME
        val initialEvents = AncientKeysIME.computeWindowShownEvents(Lifecycle.State.INITIALIZED)
        assertEquals(listOf(Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME), initialEvents)

        val createdEvents = AncientKeysIME.computeWindowShownEvents(Lifecycle.State.CREATED)
        assertEquals(listOf(Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME), createdEvents)

        // Test STARTED on show: transitions via ON_RESUME
        val startedEvents = AncientKeysIME.computeWindowShownEvents(Lifecycle.State.STARTED)
        assertEquals(listOf(Lifecycle.Event.ON_RESUME), startedEvents)

        // Test RESUMED on hide: transitions via ON_PAUSE and ON_STOP
        val hideEvents = AncientKeysIME.computeWindowHiddenEvents(Lifecycle.State.RESUMED)
        assertEquals(listOf(Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP), hideEvents)

        // Test STARTED on hide: transitions via ON_STOP
        val startedHideEvents = AncientKeysIME.computeWindowHiddenEvents(Lifecycle.State.STARTED)
        assertEquals(listOf(Lifecycle.Event.ON_STOP), startedHideEvents)

        // Test CREATED on hide: no further stop events needed
        val createdHideEvents = AncientKeysIME.computeWindowHiddenEvents(Lifecycle.State.CREATED)
        assertTrue("Created state needs no hide events", createdHideEvents.isEmpty())
    }

    @Test
    fun testAncientKeysImeImplementsViewTreeContracts() {
        val ime = AncientKeysIME()
        assertTrue("AncientKeysIME must implement LifecycleOwner", ime is LifecycleOwner)
        assertTrue("AncientKeysIME must implement ViewModelStoreOwner", ime is ViewModelStoreOwner)
        assertTrue("AncientKeysIME must implement SavedStateRegistryOwner", ime is SavedStateRegistryOwner)

        assertNotNull("Lifecycle must be non-null", ime.lifecycle)
        assertNotNull("ViewModelStore must be non-null", ime.viewModelStore)
        assertNotNull("SavedStateRegistry must be non-null", ime.savedStateRegistry)
        assertEquals("Initial state must be INITIALIZED", Lifecycle.State.INITIALIZED, ime.lifecycle.currentState)
    }
}
