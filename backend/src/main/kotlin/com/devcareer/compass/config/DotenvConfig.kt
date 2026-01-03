package com.devcareer.compass.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import java.io.File

class DotenvConfig : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        // 여러 경로 시도
        val possiblePaths = listOf(
            "./backend",
            ".",
            "../",
            System.getProperty("user.dir") + "/backend"
        )

        val dotenv = possiblePaths
            .firstNotNullOfOrNull { path ->
                try {
                    if (File(path, ".env").exists()) {
                        Dotenv.configure()
                            .directory(path)
                            .load()
                    } else null
                } catch (e: Exception) {
                    null
                }
            } ?: Dotenv.configure().ignoreIfMissing().load()

        val dotenvProperties = dotenv.entries()
            .associate { it.key to it.value }

        applicationContext.environment.propertySources
            .addFirst(MapPropertySource("dotenvProperties", dotenvProperties))
    }
}