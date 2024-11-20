package org.prinstcript10.snippetrunner.redis.testCase.event

data class TestCaseResponseEvent(
    val testCaseId: String,
    val outputs: List<String>,
    val errors: List<String>?,
)
