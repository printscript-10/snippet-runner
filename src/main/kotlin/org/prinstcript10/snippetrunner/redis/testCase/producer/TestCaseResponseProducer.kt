package org.prinstcript10.snippetrunner.redis.testCase.producer

import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class TestCaseResponseProducer
    @Autowired
    constructor(
        @Value("\${test_response}")
        private val streamName: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamName, redis) {

        suspend fun publishEvent(event: String) {
            println("Publishing test case response: $event")
            emit(event)
        }
    }
