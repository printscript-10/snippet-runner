package org.prinstcript10.snippetrunner.runner.model.dto

import linter.LinterConfig

data class LintSnippetDTO(
    val snippet: String,
    val config: LinterConfig,
)
