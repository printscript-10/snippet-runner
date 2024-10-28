package org.prinstcript10.snippetrunner.runner.model.handlers

import provider.ErrorHandler

class RunnerErrorHandler : ErrorHandler {
    val errors: MutableList<String> = mutableListOf()

    override fun reportError(message: String) {
        errors.add(message)
    }
}
