package org.prinstcript10.snippetrunner.redis.lint.event

data class LintResponseEvent(
    val snippetId: String,
    val userId: String,
    val status: SnippetLintStatus,
)
