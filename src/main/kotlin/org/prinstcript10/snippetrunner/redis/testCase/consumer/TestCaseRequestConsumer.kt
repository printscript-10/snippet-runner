package org.prinstcript10.snippetrunner.redis.testCase.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
import org.austral.ingsis.redis.RedisStreamConsumer
import org.prinstcript10.snippetrunner.redis.testCase.event.TestCaseRequestEvent
import org.prinstcript10.snippetrunner.redis.testCase.event.TestCaseResponseEvent
import org.prinstcript10.snippetrunner.redis.testCase.producer.TestCaseResponseProducer
import org.prinstcript10.snippetrunner.runner.model.dto.RunSnippetDTO
import org.prinstcript10.snippetrunner.runner.service.RunnerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component

@Component
class TestCaseRequestConsumer
    @Autowired
    constructor(
        @Value("\${test_request}")
        private val streamName: String,
        @Value("\${testando_ando}")
        private val groupName: String,
        redis: RedisTemplate<String, String>,
        private val objectMapper: ObjectMapper,
        private val runnerService: RunnerService,
        private val testCaseResponseProducer: TestCaseResponseProducer,
    ) : RedisStreamConsumer<String>(streamName, groupName, redis) {

        override fun onMessage(record: ObjectRecord<String, String>) {
            val testCaseRequest: TestCaseRequestEvent = objectMapper.readValue(record.value)

            println("Received test case request: $testCaseRequest")

            try {
                val testCaseResponse = runnerService.runSnippet(
                    RunSnippetDTO(testCaseRequest.snippetId, testCaseRequest.inputs),
                )

                runBlocking {
                    testCaseResponseProducer.publishEvent(
                        objectMapper.writeValueAsString(
                            TestCaseResponseEvent(
                                testCaseId = testCaseRequest.testCaseId,
                                outputs = testCaseResponse.outputs,
                                errors = testCaseResponse.errors,
                            ),
                        ),
                    )
                }
            } catch (e: Exception) {
                runBlocking {
                    testCaseResponseProducer.publishEvent(
                        objectMapper.writeValueAsString(
                            TestCaseResponseEvent(
                                testCaseId = testCaseRequest.testCaseId,
                                outputs = listOf(),
                                errors = listOf(e.toString()),
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
