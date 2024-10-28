package org.prinstcript10.snippetrunner.runner.controller

import jakarta.validation.Valid
import org.prinstcript10.snippetrunner.runner.model.dto.ValidateSnippetDTO
import org.prinstcript10.snippetrunner.runner.service.RunnerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("runner")
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
}
