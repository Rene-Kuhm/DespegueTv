package com.despegue.tv.security

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Test

class SecurityConfigurationTest {

    @Test
    fun `source code does not disable TLS verification`() {
        val source = File("src/main/java/com/despegue/tv")
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .joinToString(separator = "\n") { it.readText() }

        assertFalse(
            "App source must not trust every certificate.",
            source.contains("checkServerTrusted") && source.contains("= Unit")
        )
        assertFalse(
            "App source must not accept every hostname.",
            source.contains("hostnameVerifier { _, _ -> true }")
        )
    }

    @Test
    fun `build script does not contain fallback release signing secrets`() {
        val source = File("build.gradle.kts").readText()

        assertFalse(
            "Release signing passwords must come from env vars or local.properties only.",
            source.contains("815787")
        )
    }

    @Test
    fun `youtube extractor does not embed fallback api keys`() {
        val source = File("src/main/java/com/despegue/tv/data/trailer/InAppYouTubeExtractor.kt").readText()

        assertFalse(
            "YouTube API keys must not be embedded in source code.",
            source.contains("AIza")
        )
    }
}
