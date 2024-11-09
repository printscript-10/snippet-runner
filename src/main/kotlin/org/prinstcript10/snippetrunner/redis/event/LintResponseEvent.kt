package org.prinstcript10.snippetrunner.redis.event

data class LintResponseEvent(
    val snippetId: String,
    val status: SnippetLintStatus,
)
