package org.prinstcript10.snippetrunner.runner.model.handlers

import provider.OutputProvider

class RunnerOutputProvider : OutputProvider {
    val outputs: MutableList<String> = mutableListOf()
    override fun print(message: String) {
        outputs.add(message)
    }
}
