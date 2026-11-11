package com.ancientkeys.keyboard

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import com.ancientkeys.keyboard.ime.AncientKeysIME
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ImeServiceLifecycleTest {

    @Test
    fun testAncientKeysImeImplementsRequiredOwners() {
        // Verify class-level architectural contracts for ViewTree compatibility
        val clazz = AncientKeysIME::class.java

        assertTrue(
            "AncientKeysIME must implement LifecycleOwner for Compose",
            LifecycleOwner::class.java.isAssignableFrom(clazz)
        )
        assertTrue(
            "AncientKeysIME must implement ViewModelStoreOwner for Compose",
            ViewModelStoreOwner::class.java.isAssignableFrom(clazz)
        )
        assertTrue(
            "AncientKeysIME must implement SavedStateRegistryOwner for Compose",
            SavedStateRegistryOwner::class.java.isAssignableFrom(clazz)
        )
    }

    @Test
    fun testAncientKeysImeLifecycleConstants() {
        val ime = AncientKeysIME()
        assertNotNull("lifecycle should not be null", ime.lifecycle)
        assertNotNull("viewModelStore should not be null", ime.viewModelStore)
        assertNotNull("savedStateRegistry should not be null", ime.savedStateRegistry)

        // Verify initial lifecycle state
        assertTrue(
            "Initial state should be INITIALIZED",
            ime.lifecycle.currentState == Lifecycle.State.INITIALIZED
        )
    }
}

