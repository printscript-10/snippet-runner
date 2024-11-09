package org.prinstcript10.snippetrunner.runner.model.dto

data class LintSnippetResponseDTO(
    val success: Boolean,
    val errors: List<String>,
)
