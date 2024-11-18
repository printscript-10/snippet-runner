package org.prinstcript10.snippetrunner.redis.lint.event

import linter.LinterConfig

data class LintRequestEvent(
    val snippetId: String,
    val userId: String,
    val config: LinterConfig,
)
