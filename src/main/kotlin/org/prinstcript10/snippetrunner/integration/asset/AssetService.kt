package org.prinstcript10.snippetrunner.integration.asset

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class AssetService
    @Autowired
    constructor(
        private val rest: RestTemplate,
        @Value("\${balde_url}")
        private val bucketUrl: String,
    ) {

        fun getSnippet(
            snippetId: String,
        ): String {
            return rest.getForEntity("$bucketUrl/$snippetId", String::class.java).body!!
        }
    }
