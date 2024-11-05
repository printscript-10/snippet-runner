package org.prinstcript10.snippetrunner.runner.model.dto

data class RunSnippetResponseDTO(
    val outputs: List<String>,
    val errors: List<String>,
)
