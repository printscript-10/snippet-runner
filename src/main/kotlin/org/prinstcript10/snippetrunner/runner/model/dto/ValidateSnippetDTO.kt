package org.prinstcript10.snippetrunner.runner.model.dto

import jakarta.validation.constraints.NotBlank

data class ValidateSnippetDTO(
    @NotBlank(message = "Snippet is required")
    val snippet: String,
)
