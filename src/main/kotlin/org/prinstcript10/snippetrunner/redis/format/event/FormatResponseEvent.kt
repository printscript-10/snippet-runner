package org.prinstcript10.snippetrunner.redis.format.event

data class FormatResponseEvent(
    val snippetId: String,
    val userId: String,
    val status: SnippetFormatStatus,
    val formattedSnippet: String?,
)
