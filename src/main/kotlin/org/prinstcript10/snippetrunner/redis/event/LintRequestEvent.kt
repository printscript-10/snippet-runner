package org.prinstcript10.snippetrunner.redis.event

import linter.LinterConfig

data class LintRequestEvent(
    val snippetId: String,
    val config: LinterConfig,
)
