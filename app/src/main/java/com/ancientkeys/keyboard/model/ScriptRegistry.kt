package com.ancientkeys.keyboard.model

import com.ancientkeys.keyboard.data.AncientGreekScript
import com.ancientkeys.keyboard.data.BrahmiScript
import com.ancientkeys.keyboard.data.EgyptianHieroglyphsScript
import com.ancientkeys.keyboard.data.KharosthiScript
import com.ancientkeys.keyboard.data.PhoenicianScript
import com.ancientkeys.keyboard.data.RunicScript

/**
 * Central registry of all supported historical writing systems in AncientKeys.
 */
object ScriptRegistry {

    val allScripts: List<HistoricalScript> = listOf(
        EgyptianHieroglyphsScript,
        BrahmiScript,
        AncientGreekScript,
        RunicScript,
        PhoenicianScript,
        KharosthiScript
    )

    fun getScriptById(id: String): HistoricalScript? {
        return allScripts.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }
}

