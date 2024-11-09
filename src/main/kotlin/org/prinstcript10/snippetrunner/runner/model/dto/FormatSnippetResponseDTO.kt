package org.prinstcript10.snippetrunner.runner.model.dto

data class FormatSnippetResponseDTO(
    val formattedSnippet: String?,
    val errors: List<String>,
)
