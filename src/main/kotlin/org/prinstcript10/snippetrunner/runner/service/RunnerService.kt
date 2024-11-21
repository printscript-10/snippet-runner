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
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import runner.Runner
import java.io.InputStream

@Service
class RunnerService(
    @Autowired private val assetService: AssetService,
) {
    private val runner: Runner = Runner("1.1")
    private val logger = LoggerFactory.getLogger(RunnerService::class.java)

    fun validateSnippet(snippet: ValidateSnippetDTO) {
        logger.info("validating snippet: ${snippet.snippet}")
        val inputStream: InputStream = snippet.snippet.byteInputStream()
        val errorHandler = RunnerErrorHandler()
        runner.validate(inputStream, errorHandler)

        if (errorHandler.errors.isNotEmpty()) {
            logger.error("Error validating snippet: ${errorHandler.errors.first()}")
            throw BadRequestException(errorHandler.errors.joinToString("\n"))
        }
        logger.info("Snippet validated successfully")
    }

    fun runSnippet(runSnippetDTO: RunSnippetDTO): RunSnippetResponseDTO {
        logger.info("Running snippet: ${runSnippetDTO.snippetId}")
        val snippetValue: String = assetService.getSnippet(runSnippetDTO.snippetId)

        val inputStream: InputStream = snippetValue.byteInputStream()

        val inputProvider = RunnerInputProvider(runSnippetDTO.inputs)
        val outputProvider = RunnerOutputProvider()
        val readEnvProvider = RunnerReadEnvProvider()
        val errorHandler = RunnerErrorHandler()

        runner.execute(inputStream, outputProvider, errorHandler, inputProvider, readEnvProvider)
        logger.info("Snippet execution completed")

        return RunSnippetResponseDTO(
            outputs = outputProvider.outputs,
            errors = errorHandler.errors,
        )
    }

    fun formatSnippet(formatSnippetDTO: FormatSnippetDTO): FormatSnippetResponseDTO {
        logger.info("Formatting snippet: ${formatSnippetDTO.snippet}")
        val inputStream: InputStream = formatSnippetDTO.snippet.byteInputStream()
        val outputProvider = RunnerOutputProvider()
        val errorHandler = RunnerErrorHandler()

        val result = runner.format(inputStream, errorHandler, formatSnippetDTO.config, outputProvider)
        logger.info("Snippet format completed")

        return FormatSnippetResponseDTO(
            formattedSnippet = result,
            errors = errorHandler.errors,
        )
    }

    fun lintSnippet(lintSnippetDTO: LintSnippetDTO): LintSnippetResponseDTO {
        logger.info("Linting snippet: ${lintSnippetDTO.snippet}")
        val inputStream: InputStream = lintSnippetDTO.snippet.byteInputStream()
        val outputProvider = RunnerOutputProvider()
        val errorHandler = RunnerErrorHandler()

        runner.analyze(inputStream, errorHandler, lintSnippetDTO.config, outputProvider)
        logger.info("Snippet lint completed")

        return LintSnippetResponseDTO(
            success = errorHandler.errors.isEmpty(),
            errors = errorHandler.errors,
        )
    }
}
