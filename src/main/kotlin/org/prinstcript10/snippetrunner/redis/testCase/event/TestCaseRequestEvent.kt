package org.prinstcript10.snippetrunner.redis.testCase.event

data class TestCaseRequestEvent(
    val testCaseId: String,
    val snippetId: String,
    val inputs: List<String>,
)
