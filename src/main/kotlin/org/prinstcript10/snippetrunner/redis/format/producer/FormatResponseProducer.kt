package org.prinstcript10.snippetrunner.redis.format.producer

import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class FormatResponseProducer
    @Autowired
    constructor(
        @Value("\${format_response}")
        private val streamName: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamName, redis) {

        suspend fun publishEvent(event: String) {
            println("Publishing format response: $event")
            emit(event)
        }
    }
