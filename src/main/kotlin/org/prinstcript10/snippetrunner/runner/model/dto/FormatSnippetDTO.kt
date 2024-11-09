package org.prinstcript10.snippetrunner.runner.model.dto

import formatter.FormatterConfig

data class FormatSnippetDTO(
    val snippet: String,
    val config: FormatterConfig,
)
