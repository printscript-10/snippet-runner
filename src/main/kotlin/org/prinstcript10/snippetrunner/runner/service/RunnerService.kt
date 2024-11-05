package org.prinstcript10.snippetrunner.runner.service

import org.prinstcript10.snippetrunner.integration.asset.AssetService
import org.prinstcript10.snippetrunner.runner.model.dto.RunSnippetDTO
import org.prinstcript10.snippetrunner.runner.model.dto.RunSnippetResponseDTO
import org.prinstcript10.snippetrunner.runner.model.dto.ValidateSnippetDTO
import org.prinstcript10.snippetrunner.runner.model.handlers.RunnerErrorHandler
import org.prinstcript10.snippetrunner.runner.model.handlers.RunnerInputProvider
import org.prinstcript10.snippetrunner.runner.model.handlers.RunnerOutputProvider
import org.prinstcript10.snippetrunner.runner.model.handlers.RunnerReadEnvProvider
import org.prinstcript10.snippetrunner.shared.exception.BadRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import runner.Runner
import java.io.InputStream

@Service
class RunnerService(
    @Autowired private val assetService: AssetService,
) {
    private val runner: Runner = Runner("1.1")

    fun validateSnippet(snippet: ValidateSnippetDTO) {
        val inputStream: InputStream = snippet.snippet.byteInputStream()
        val errorHandler = RunnerErrorHandler()
        runner.validate(inputStream, errorHandler)

        if (errorHandler.errors.isNotEmpty()) {
            throw BadRequestException(errorHandler.errors.joinToString("\n"))
        }
    }

    fun runSnippet(snippetId: String, snippet: RunSnippetDTO): RunSnippetResponseDTO {
        val snippetValue: String = assetService.getSnippet(snippetId)

        val inputStream: InputStream = snippetValue.byteInputStream()

        val inputProvider = RunnerInputProvider(snippet.inputs)
        val outputProvider = RunnerOutputProvider()
        val readEnvProvider = RunnerReadEnvProvider()
        val errorHandler = RunnerErrorHandler()

        runner.execute(inputStream, outputProvider, errorHandler, inputProvider, readEnvProvider)

        return RunSnippetResponseDTO(
            outputs = outputProvider.outputs,
            errors = errorHandler.errors,
        )
    }
}
