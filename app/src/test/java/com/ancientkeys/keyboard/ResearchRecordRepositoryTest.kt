package com.ancientkeys.keyboard

import com.ancientkeys.keyboard.model.ResearchRecord
import com.ancientkeys.keyboard.storage.ResearchRecordJson
import com.ancientkeys.keyboard.storage.ResearchRecordRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

/**
 * Unit tests for epigraphic research record persistence, Markdown export, and JSON serialization.
 */
class ResearchRecordRepositoryTest {

    private lateinit var memoryRepo: ResearchRecordRepository

    @Before
    fun setUp() {
        memoryRepo = ResearchRecordRepository(null)
    }

    @Test
    fun testSaveAndRetrieveRecord() {
        val record = ResearchRecord(
            id = "girnar_edicts_13",
            title = "Major Rock Edict XIII (Girnar)",
            scriptId = "brahmi",
            originalText = "𑀤𑁂𑀯𑀸𑀦𑀁𑀧𑁆𑀭𑀺𑀬𑀲 𑀧𑁆𑀭𑀺𑀬𑀤𑀭𑁆𑀰𑀺𑀦𑁄 𑀭𑀸𑀜𑁄",
            academicTransliteration = "devānaṃpriyasa priyadarśino rāño",
            researcherTranslation = "King Priyadarsi, Beloved of the Gods...",
            sourceReference = "Corpus Inscriptionum Indicarum Vol. I, p. 20",
            notes = "Rock inscription in Junagadh, Gujarat. Early Prakrit dialect."
        )

        assertTrue(memoryRepo.saveRecord(record))
        val retrieved = memoryRepo.getRecordById("girnar_edicts_13")
        assertNotNull(retrieved)
        assertEquals("Major Rock Edict XIII (Girnar)", retrieved?.title)
        assertEquals("brahmi", retrieved?.scriptId)
        assertEquals("𑀤𑁂𑀯𑀸𑀦𑀁𑀧𑁆𑀭𑀺𑀬𑀲 𑀧𑁆𑀭𑀺𑀬𑀤𑀭𑁆𑀰𑀺𑀦𑁄 𑀭𑀸𑀜𑁄", retrieved?.originalText)
        assertEquals("devānaṃpriyasa priyadarśino rāño", retrieved?.academicTransliteration)
        assertEquals("King Priyadarsi, Beloved of the Gods...", retrieved?.researcherTranslation)
        assertEquals("Corpus Inscriptionum Indicarum Vol. I, p. 20", retrieved?.sourceReference)
    }

    @Test
    fun testUpdateExistingRecord() {
        val record = ResearchRecord(
            id = "rec_001",
            title = "Initial Title",
            scriptId = "greek",
            originalText = "Α Β Γ",
            academicTransliteration = "a b g",
            researcherTranslation = "alpha beta gamma"
        )
        memoryRepo.saveRecord(record)
        assertEquals(1, memoryRepo.getAllRecords().size)

        val updated = record.copy(title = "Updated Title", originalText = "Α Β Γ Δ")
        memoryRepo.saveRecord(updated)

        assertEquals(1, memoryRepo.getAllRecords().size)
        val fetched = memoryRepo.getRecordById("rec_001")
        assertEquals("Updated Title", fetched?.title)
        assertEquals("Α Β Γ Δ", fetched?.originalText)
    }

    @Test
    fun testDeleteRecord() {
        val record = ResearchRecord(
            id = "del_me",
            title = "To be deleted",
            scriptId = "runic",
            originalText = "ᚠᚢᚦ",
            academicTransliteration = "fuþ",
            researcherTranslation = "wealth ox giant"
        )
        memoryRepo.saveRecord(record)
        assertNotNull(memoryRepo.getRecordById("del_me"))

        val deleted = memoryRepo.deleteRecord("del_me")
        assertTrue(deleted)
        assertNull(memoryRepo.getRecordById("del_me"))
    }

    @Test
    fun testExportToMarkdownContainsScholarlyDisclaimer() {
        val record = ResearchRecord(
            id = "rec_md",
            title = "Test Inscription",
            scriptId = "kharosthi",
            originalText = "𐨐𐨁𐨟",
            academicTransliteration = "kita",
            researcherTranslation = "Prakrit verbal adjective 'done'",
            sourceReference = "Gandharan Scroll Frag. 12",
            notes = "Birch-bark manuscript"
        )
        val md = memoryRepo.exportToMarkdown(record)

        assertTrue(md.contains("# Test Inscription"))
        assertTrue(md.contains("Gandharan Scroll Frag. 12"))
        assertTrue(md.contains("𐨐𐨁𐨟"))
        assertTrue(md.contains("kita"))
        // Explicitly assert the translation disclaimer is embedded
        assertTrue("Markdown must clearly disclaim automated translation", md.contains("AncientKeys does not perform automated machine translation"))
        assertTrue(md.contains("Prakrit verbal adjective 'done'"))
    }

    @Test
    fun testExportToJsonValidStructure() {
        val r1 = ResearchRecord(
            id = "json_1",
            title = "Json Record 1",
            scriptId = "phoenician",
            originalText = "𐤀𐤁𐤂",
            academicTransliteration = "ʾbg",
            researcherTranslation = "ox house camel"
        )
        memoryRepo.saveRecord(r1)

        val jsonString = memoryRepo.exportToJson()
        assertTrue("JSON string should contain ID", jsonString.contains("\"id\": \"json_1\""))
        assertTrue("JSON string should contain title", jsonString.contains("\"title\": \"Json Record 1\""))
        assertTrue("JSON string should contain scriptId", jsonString.contains("\"scriptId\": \"phoenician\""))
        assertTrue("JSON string should contain originalText", jsonString.contains("\"originalText\": \"𐤀𐤁𐤂\""))

        val parsed = ResearchRecordJson.parse(jsonString)
        assertEquals(1, parsed.size)
        assertEquals("json_1", parsed[0].id)
        assertEquals("Json Record 1", parsed[0].title)
        assertEquals("phoenician", parsed[0].scriptId)
        assertEquals("𐤀𐤁𐤂", parsed[0].originalText)
        assertEquals("ʾbg", parsed[0].academicTransliteration)
        assertEquals("ox house camel", parsed[0].researcherTranslation)
    }

    @Test
    fun testFilePersistenceWithTempDirectory() {
        val tempDir = Files.createTempDirectory("ancientkeys_repo_test").toFile()
        try {
            val fileRepo = ResearchRecordRepository(overrideDirectory = tempDir)
            val record = ResearchRecord(
                id = "disk_001",
                title = "Disk Persisted Inscription",
                scriptId = "egyptian",
                originalText = "𓇳𓏤",
                academicTransliteration = "rꜥ",
                researcherTranslation = "Sun god Ra"
            )

            assertTrue(fileRepo.saveRecord(record))

            // Create a second repository instance pointing to same directory to verify disk reading
            val secondRepo = ResearchRecordRepository(overrideDirectory = tempDir)
            val loaded = secondRepo.getRecordById("disk_001")
            assertNotNull(loaded)
            assertEquals("Disk Persisted Inscription", loaded?.title)
            assertEquals("𓇳𓏤", loaded?.originalText)
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun testMalformedJsonParsingReturnsEmptyList() {
        // Completely invalid input
        assertEquals(emptyList<ResearchRecord>(), ResearchRecordJson.parse(""))
        assertEquals(emptyList<ResearchRecord>(), ResearchRecordJson.parse("NOT_JSON"))
        assertEquals(emptyList<ResearchRecord>(), ResearchRecordJson.parse("{ \"not\": \"an array\" }"))

        // Truncated / malformed JSON array
        assertEquals(emptyList<ResearchRecord>(), ResearchRecordJson.parse("["))
        assertEquals(emptyList<ResearchRecord>(), ResearchRecordJson.parse("[{\"id\": \"incomplete\""))
        assertEquals(emptyList<ResearchRecord>(), ResearchRecordJson.parse("[{\"id\": \"corrupted\", }"))
    }

    @Test
    fun testUnusualUnicodeAndQuotesPreservation() {
        val complexRecord = ResearchRecord(
            id = "unicode_extreme",
            title = "Unusual Unicode & Quotes: \"Quotes\", 'Single', \\ Backslash",
            scriptId = "egyptian",
            // SMP Egyptian + Brahmi + RTL Phoenician + Combining marks
            originalText = "𓇳𓏤 𑀅𑀰𑁄𑀓 𐤀𐤁𐤂 𐨐𐨁𐨟\uD804\uDC38\uD804\uDC46",
            academicTransliteration = "rꜥ / aśoka / ʾbg / kita\nWith Multi-line Commentary",
            researcherTranslation = "Line 1: \"Translation with quotes\"\nLine 2: Tab\there and symbols: & < >",
            sourceReference = "Corpus Inscriptionum Semiticarum / BM EA24",
            notes = "Special notes: emoji 📜🏺 and unicode surrogate pairs."
        )

        val json = ResearchRecordJson.serialize(listOf(complexRecord))
        val parsed = ResearchRecordJson.parse(json)

        assertEquals(1, parsed.size)
        val roundtrip = parsed[0]
        assertEquals(complexRecord.id, roundtrip.id)
        assertEquals(complexRecord.title, roundtrip.title)
        assertEquals(complexRecord.originalText, roundtrip.originalText)
        assertEquals(complexRecord.academicTransliteration, roundtrip.academicTransliteration)
        assertEquals(complexRecord.researcherTranslation, roundtrip.researcherTranslation)
        assertEquals(complexRecord.notes, roundtrip.notes)

        // Verify Markdown export formats properly without throwing
        val markdown = memoryRepo.exportToMarkdown(roundtrip)
        assertTrue(markdown.contains("𓇳𓏤 𑀅𑀰𑁄𑀓 𐤀𐤁𐤂"))
        assertTrue(markdown.contains("📜🏺"))
    }

    @Test
    fun testClearAllRecordsDeletesDiskFile() {
        val tempDir = Files.createTempDirectory("ancientkeys_clear_test").toFile()
        try {
            val fileRepo = ResearchRecordRepository(overrideDirectory = tempDir)
            val record = ResearchRecord(
                id = "r_clear",
                title = "Temp",
                scriptId = "greek",
                originalText = "Ω",
                academicTransliteration = "ō",
                researcherTranslation = "omega"
            )
            fileRepo.saveRecord(record)
            assertEquals(1, fileRepo.getAllRecords().size)

            val jsonFile = File(tempDir, "research_records.json")
            assertTrue("JSON file must exist on disk after saving", jsonFile.exists())

            assertTrue(fileRepo.clearAllRecords())
            assertEquals(0, fileRepo.getAllRecords().size)
            assertFalse("JSON file must be deleted after clearing all records", jsonFile.exists())
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun testEmptyRepositoryMarkdownExport() {
        val emptyMd = memoryRepo.exportAllToMarkdown()
        assertTrue(emptyMd.contains("*(No records saved)*"))
    }
}

