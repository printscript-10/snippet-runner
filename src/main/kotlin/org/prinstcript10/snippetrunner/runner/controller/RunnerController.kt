package org.prinstcript10.snippetrunner.runner.controller

import jakarta.validation.Valid
import org.prinstcript10.snippetrunner.runner.model.dto.FormatSnippetDTO
import org.prinstcript10.snippetrunner.runner.model.dto.FormatSnippetResponseDTO
import org.prinstcript10.snippetrunner.runner.model.dto.LintSnippetDTO
import org.prinstcript10.snippetrunner.runner.model.dto.LintSnippetResponseDTO
import org.prinstcript10.snippetrunner.runner.model.dto.RunSnippetDTO
import org.prinstcript10.snippetrunner.runner.model.dto.RunSnippetResponseDTO
import org.prinstcript10.snippetrunner.runner.model.dto.ValidateSnippetDTO
import org.prinstcript10.snippetrunner.runner.service.RunnerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("runner")
@Validated
class RunnerController(
    @Autowired
    private val runnerService: RunnerService,
) {

    @PutMapping("/validate")
    fun validateSnippet(
        @Valid @RequestBody snippetDTO: ValidateSnippetDTO,
    ) {
        return runnerService.validateSnippet(snippetDTO)
    }

    @PostMapping("/run")
    fun runSnippet(
        @Valid @RequestBody runSnippetDTO: RunSnippetDTO,
    ): ResponseEntity<RunSnippetResponseDTO> {
        return ResponseEntity.ok(runnerService.runSnippet(runSnippetDTO))
    }

    @PostMapping("/format")
    fun formatSnippet(
        @Valid @RequestBody formatSnippetDTO: FormatSnippetDTO,
    ): FormatSnippetResponseDTO {
        return runnerService.formatSnippet(formatSnippetDTO)
    }

    @PostMapping("/lint")
    fun lintSnippet(
        @Valid @RequestBody lintSnippetDTO: LintSnippetDTO,
    ): LintSnippetResponseDTO {
        return runnerService.lintSnippet(lintSnippetDTO)
    }
}
