package org.prinstcript10.snippetrunner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SnippetRunnerApplication

fun main(args: Array<String>) {
    runApplication<SnippetRunnerApplication>(*args)
}
