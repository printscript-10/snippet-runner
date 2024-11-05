package org.prinstcript10.snippetrunner.runner.model.handlers

import provider.InputProvider

class RunnerInputProvider(
    private val inputs: List<String>,
) : InputProvider {

    private var index: Int = 0
    override fun readInput(name: String): String? {
        val res = inputs[index]
        this.index += 1
        return res
    }
}
