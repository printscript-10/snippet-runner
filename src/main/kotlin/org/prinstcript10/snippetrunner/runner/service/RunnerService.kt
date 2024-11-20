package org.prinstcript10.snippetrunner.runner.service

import org.prinstcript10.snippetrunner.integration.asset.AssetService
import org.prinstcript10.snippetrunner.runner.model.dto.FormatSnippetDTO
import org.prinstcript10.snippetrunner.runner.model.dto.FormatSnippetResponseDTO
import org.prinstcript10.snippetrunner.runner.model.dto.LintSnippetDTO
import org.prinstcript10.snippetrunner.runner.model.dto.LintSnippetResponseDTO
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

    fun runSnippet(runSnippetDTO: RunSnippetDTO): RunSnippetResponseDTO {
        val snippetValue: String = assetService.getSnippet(runSnippetDTO.snippetId)

        val inputStream: InputStream = snippetValue.byteInputStream()

        val inputProvider = RunnerInputProvider(runSnippetDTO.inputs)
        val outputProvider = RunnerOutputProvider()
        val readEnvProvider = RunnerReadEnvProvider()
        val errorHandler = RunnerErrorHandler()

        runner.execute(inputStream, outputProvider, errorHandler, inputProvider, readEnvProvider)

        return RunSnippetResponseDTO(
            outputs = outputProvider.outputs,
            errors = errorHandler.errors,
        )
    }

    fun formatSnippet(formatSnippetDTO: FormatSnippetDTO): FormatSnippetResponseDTO {
        val inputStream: InputStream = formatSnippetDTO.snippet.byteInputStream()
        val outputProvider = RunnerOutputProvider()
        val errorHandler = RunnerErrorHandler()

        val result = runner.format(inputStream, errorHandler, formatSnippetDTO.config, outputProvider)

        return FormatSnippetResponseDTO(
            formattedSnippet = result,
            errors = errorHandler.errors,
        )
    }

    fun lintSnippet(lintSnippetDTO: LintSnippetDTO): LintSnippetResponseDTO {
        val inputStream: InputStream = lintSnippetDTO.snippet.byteInputStream()
        val outputProvider = RunnerOutputProvider()
        val errorHandler = RunnerErrorHandler()

        runner.analyze(inputStream, errorHandler, lintSnippetDTO.config, outputProvider)

        return LintSnippetResponseDTO(
            success = errorHandler.errors.isEmpty(),
            errors = errorHandler.errors,
        )
    }
}
