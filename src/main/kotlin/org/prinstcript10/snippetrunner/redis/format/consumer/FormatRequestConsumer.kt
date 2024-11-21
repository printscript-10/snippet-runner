package org.prinstcript10.snippetrunner.redis.format.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import org.austral.ingsis.redis.RedisStreamConsumer
import org.prinstcript10.snippetrunner.integration.asset.AssetService
import org.prinstcript10.snippetrunner.redis.format.event.FormatRequestEvent
import org.prinstcript10.snippetrunner.redis.format.event.FormatResponseEvent
import org.prinstcript10.snippetrunner.redis.format.event.SnippetFormatStatus
import org.prinstcript10.snippetrunner.redis.format.producer.FormatResponseProducer
import org.prinstcript10.snippetrunner.runner.model.dto.FormatSnippetDTO
import org.prinstcript10.snippetrunner.runner.service.RunnerService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component

@Component
class FormatRequestConsumer
    @Autowired
    constructor(
        @Value("\${format_request}")
        private val streamName: String,
        @Value("\${formateando_ando}")
        private val groupName: String,
        redis: RedisTemplate<String, String>,
        private val objectMapper: ObjectMapper,
        private val assetService: AssetService,
        private val runnerService: RunnerService,
        private val formatResponseProducer: FormatResponseProducer,
    ) : RedisStreamConsumer<String>(streamName, groupName, redis) {

        private val logger = LoggerFactory.getLogger(FormatRequestConsumer::class.java)

        override fun onMessage(record: ObjectRecord<String, String>) {
            val formatRequest: FormatRequestEvent = objectMapper.readValue(record.value)

            logger.info("Received format request: $formatRequest")
            val asset: String = assetService.getSnippet(formatRequest.snippetId)

            try {
                val formatResponse = runnerService.formatSnippet(FormatSnippetDTO(asset, formatRequest.config))

                val formatStatus: SnippetFormatStatus =
                    if (formatResponse.errors.isEmpty()) SnippetFormatStatus.SUCCESS else SnippetFormatStatus.FAILED

                runBlocking {
                    formatResponseProducer.publishEvent(
                        objectMapper.writeValueAsString(
                            FormatResponseEvent(
                                snippetId = formatRequest.snippetId,
                                userId = formatRequest.userId,
                                status = formatStatus,
                                formattedSnippet = formatResponse.formattedSnippet,
                            ),
                        ),
                    )
                }
            } catch (e: Exception) {
                logger.error("Error while consuming format request:", e)
                runBlocking {
                    formatResponseProducer.publishEvent(
                        objectMapper.writeValueAsString(
                            FormatResponseEvent(
                                snippetId = formatRequest.snippetId,
                                userId = formatRequest.userId,
                                status = SnippetFormatStatus.FAILED,
                                formattedSnippet = null,
                            ),
                        ),
                    )
                }
            } finally {
                logger.info("Format request consumed successfully: $formatRequest")
            }
        }

        override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
            return StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(java.time.Duration.ofMillis(50000))
                .targetType(String::class.java)
                .build()
        }
    }
