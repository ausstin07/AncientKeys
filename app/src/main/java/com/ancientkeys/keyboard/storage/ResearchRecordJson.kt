package com.ancientkeys.keyboard.storage

import com.ancientkeys.keyboard.model.ResearchRecord

/**
 * Pure Kotlin JSON serializer and parser for [ResearchRecord].
 *
 * Avoids Android framework dependencies (`org.json`), ensuring 100% identical,
 * deterministic behavior across both Android runtime and host JVM unit tests.
 */
object ResearchRecordJson {

    fun serialize(records: List<ResearchRecord>): String {
        val sb = StringBuilder()
        sb.append("[\n")
        for (i in records.indices) {
            val r = records[i]
            sb.append("  {\n")
            sb.append("    \"id\": \"${escape(r.id)}\",\n")
            sb.append("    \"title\": \"${escape(r.title)}\",\n")
            sb.append("    \"scriptId\": \"${escape(r.scriptId)}\",\n")
            sb.append("    \"originalText\": \"${escape(r.originalText)}\",\n")
            sb.append("    \"academicTransliteration\": \"${escape(r.academicTransliteration)}\",\n")
            sb.append("    \"researcherTranslation\": \"${escape(r.researcherTranslation)}\",\n")
            sb.append("    \"sourceReference\": \"${escape(r.sourceReference)}\",\n")
            sb.append("    \"notes\": \"${escape(r.notes)}\",\n")
            sb.append("    \"createdAtEpochMs\": ${r.createdAtEpochMs},\n")
            sb.append("    \"updatedAtEpochMs\": ${r.updatedAtEpochMs}\n")
            sb.append("  }")
            if (i < records.size - 1) {
                sb.append(",")
            }
            sb.append("\n")
        }
        sb.append("]\n")
        return sb.toString()
    }

    fun parse(json: String): List<ResearchRecord> {
        val trimmed = json.trim()
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()

        val records = mutableListOf<ResearchRecord>()
        var idx = 0
        val len = trimmed.length

        fun skipWhitespace() {
            while (idx < len && trimmed[idx].isWhitespace()) {
                idx++
            }
        }

        fun parseString(): String {
            if (idx >= len || trimmed[idx] != '"') return ""
            idx++ // skip opening quote
            val sb = StringBuilder()
            while (idx < len) {
                val c = trimmed[idx]
                if (c == '"') {
                    idx++ // skip closing quote
                    return sb.toString()
                } else if (c == '\\') {
                    idx++
                    if (idx < len) {
                        when (val esc = trimmed[idx]) {
                            '"' -> sb.append('"')
                            '\\' -> sb.append('\\')
                            '/' -> sb.append('/')
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            'u' -> {
                                if (idx + 4 < len) {
                                    val hex = trimmed.substring(idx + 1, idx + 5)
                                    val cp = hex.toIntOrNull(16)
                                    if (cp != null) {
                                        sb.append(cp.toChar())
                                        idx += 4
                                    }
                                }
                            }
                            else -> sb.append(esc)
                        }
                        idx++
                    }
                } else {
                    sb.append(c)
                    idx++
                }
            }
            return sb.toString()
        }

        fun parseNumber(): Long {
            skipWhitespace()
            val start = idx
            if (idx < len && (trimmed[idx] == '-' || trimmed[idx] == '+')) idx++
            while (idx < len && trimmed[idx].isDigit()) {
                idx++
            }
            return trimmed.substring(start, idx).toLongOrNull() ?: 0L
        }

        skipWhitespace()
        if (idx < len && trimmed[idx] == '[') idx++

        while (idx < len) {
            skipWhitespace()
            if (idx >= len || trimmed[idx] == ']') break

            if (trimmed[idx] == '{') {
                idx++ // skip '{'
                val map = mutableMapOf<String, Any>()

                while (idx < len) {
                    skipWhitespace()
                    if (idx >= len || trimmed[idx] == '}') {
                        if (idx < len && trimmed[idx] == '}') idx++
                        break
                    }

                    if (trimmed[idx] == ',') {
                        idx++
                        continue
                    }

                    if (trimmed[idx] == '"') {
                        val key = parseString()
                        skipWhitespace()
                        if (idx < len && trimmed[idx] == ':') {
                            idx++ // skip ':'
                            skipWhitespace()
                            if (idx < len && trimmed[idx] == '"') {
                                map[key] = parseString()
                            } else if (idx < len && (trimmed[idx].isDigit() || trimmed[idx] == '-')) {
                                map[key] = parseNumber()
                            } else {
                                while (idx < len && trimmed[idx] != ',' && trimmed[idx] != '}') idx++
                            }
                        }
                    } else {
                        idx++
                    }
                }

                val id = map["id"] as? String ?: ""
                if (id.isNotBlank()) {
                    records.add(
                        ResearchRecord(
                            id = id,
                            title = map["title"] as? String ?: "Untitled Inscription",
                            scriptId = map["scriptId"] as? String ?: "brahmi",
                            originalText = map["originalText"] as? String ?: "",
                            academicTransliteration = map["academicTransliteration"] as? String ?: "",
                            researcherTranslation = map["researcherTranslation"] as? String ?: "",
                            sourceReference = map["sourceReference"] as? String ?: "",
                            notes = map["notes"] as? String ?: "",
                            createdAtEpochMs = (map["createdAtEpochMs"] as? Long) ?: System.currentTimeMillis(),
                            updatedAtEpochMs = (map["updatedAtEpochMs"] as? Long) ?: System.currentTimeMillis()
                        )
                    )
                }
            } else if (trimmed[idx] == ',') {
                idx++
            } else {
                idx++
            }
        }

        return records
    }

    private fun escape(s: String): String {
        val sb = StringBuilder(s.length + 16)
        for (c in s) {
            when (c) {
                '\\' -> sb.append("\\\\")
                '\"' -> sb.append("\\\"")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> {
                    if (c.code in 0x00..0x1F) {
                        sb.append(String.format("\\u%04x", c.code))
                    } else {
                        sb.append(c)
                    }
                }
            }
        }
        return sb.toString()
    }
}

