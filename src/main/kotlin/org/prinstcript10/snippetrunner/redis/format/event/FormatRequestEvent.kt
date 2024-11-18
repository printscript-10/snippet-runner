package org.prinstcript10.snippetrunner.redis.format.event

import formatter.FormatterConfig

data class FormatRequestEvent(
    val userId: String,
    val snippetId: String,
    val config: FormatterConfig,
)
