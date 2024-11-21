package org.prinstcript10.snippetrunner.redis.lint.producer

import org.austral.ingsis.redis.RedisStreamProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class LintResponseProducer
    @Autowired
    constructor(
        @Value("\${lint_response}")
        private val streamName: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamName, redis) {

        private val logger = LoggerFactory.getLogger(LintResponseProducer::class.java)

        suspend fun publishEvent(event: String) {
            logger.info("Publishing lint response: $event")
            emit(event)
        }
    }
