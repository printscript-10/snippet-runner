package org.prinstcript10.snippetrunner.redis.lint.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import org.austral.ingsis.redis.RedisStreamConsumer
import org.prinstcript10.snippetrunner.integration.asset.AssetService
import org.prinstcript10.snippetrunner.redis.lint.event.LintRequestEvent
import org.prinstcript10.snippetrunner.redis.lint.event.LintResponseEvent
import org.prinstcript10.snippetrunner.redis.lint.event.SnippetLintStatus
import org.prinstcript10.snippetrunner.redis.lint.producer.LintResponseProducer
import org.prinstcript10.snippetrunner.runner.model.dto.LintSnippetDTO
import org.prinstcript10.snippetrunner.runner.service.RunnerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component

@Component
class LintRequestConsumer
    @Autowired
    constructor(
        @Value("\${lint_request}")
        private val streamName: String,
        @Value("\${linteando_ando}")
        private val groupName: String,
        redis: RedisTemplate<String, String>,
        private val objectMapper: ObjectMapper,
        private val assetService: AssetService,
        private val runnerService: RunnerService,
        private val lintResponseProducer: LintResponseProducer,
    ) : RedisStreamConsumer<String>(streamName, groupName, redis) {

        override fun onMessage(record: ObjectRecord<String, String>) {
            val lintRequest: LintRequestEvent = objectMapper.readValue(record.value)

            println("Received lint request: $lintRequest")
            val asset: String = assetService.getSnippet(lintRequest.snippetId)

            try {
                val lintResponse = runnerService.lintSnippet(LintSnippetDTO(asset, lintRequest.config))

                val lintStatus: SnippetLintStatus =
                    if (lintResponse.success) SnippetLintStatus.COMPLIANT else SnippetLintStatus.FAILED

                runBlocking {
                    lintResponseProducer.publishEvent(
                        objectMapper.writeValueAsString(
                            LintResponseEvent(
                                snippetId = lintRequest.snippetId,
                                userId = lintRequest.userId,
                                status = lintStatus,
                            ),
                        ),
                    )
                }
            } catch (e: Exception) {
                runBlocking {
                    lintResponseProducer.publishEvent(
                        objectMapper.writeValueAsString(
                            LintResponseEvent(
                                snippetId = lintRequest.snippetId,
                                userId = lintRequest.userId,
                                status = SnippetLintStatus.FAILED,
                            ),
                        ),
                    )
                }
            } finally {
                // LOGEAR QUE TERMINO LA CUESTION
            }
        }

        override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
            return StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(java.time.Duration.ofMillis(10000))
                .targetType(String::class.java)
                .build()
        }
    }
