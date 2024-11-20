package org.prinstcript10.snippetrunner.shared.config

import org.prinstcript10.snippetrunner.logging.OutboundInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestConfig {
    @Bean
    fun restTemplate(outboundInterceptor: OutboundInterceptor): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors.add(outboundInterceptor)
        return restTemplate
    }
}
