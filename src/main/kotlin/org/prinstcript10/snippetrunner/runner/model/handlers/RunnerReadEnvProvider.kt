package org.prinstcript10.snippetrunner.runner.model.handlers

import provider.EnvProvider

class RunnerReadEnvProvider : EnvProvider {
    override fun getEnv(variable: String): String? {
        return ""
    }
}
