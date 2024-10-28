package org.prinstcript10.snippetrunner.runner.service

import org.prinstcript10.snippetrunner.runner.model.dto.ValidateSnippetDTO
import org.prinstcript10.snippetrunner.runner.model.handlers.RunnerErrorHandler
import org.prinstcript10.snippetrunner.shared.exception.BadRequestException
import org.springframework.stereotype.Service
import runner.Runner
import java.io.InputStream

@Service
class RunnerService(
    private val runner: Runner = Runner("1.1"),
) {

    fun validateSnippet(snippet: ValidateSnippetDTO) {
        val inputStream: InputStream = snippet.snippet.byteInputStream()
        val errorHandler = RunnerErrorHandler()
        runner.validate(inputStream, errorHandler)

        if (errorHandler.errors.isNotEmpty()) {
            throw BadRequestException(errorHandler.errors.joinToString("\n"))
        }
    }
}
