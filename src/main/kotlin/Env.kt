import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

enum class EnvKey {
    POSTGRES_URL,
    POSTGRES_USER,
    POSTGRES_PASSWORD;

    companion object {
        fun fromString(key: String): EnvKey? = entries.find { it.name == key }
    }
}

object Env {
    private val values: Map<EnvKey, String> = loadEnv()

    fun get(key: EnvKey): String = values[key] ?: throw MissingEnvKeyException(key)

    private fun loadEnv(): Map<EnvKey, String> {
        val envFile = File(".env")
        if (!envFile.exists()) {
            throw EnvFileNotFoundException()
        }

        val result = mutableMapOf<EnvKey, String>()

        Files.readAllLines(Paths.get(".env")).forEach { line ->
            if (line.isBlank() || line.startsWith("#")) return@forEach

            val parts = line.split("=", limit = 2)
            if (parts.size != 2) {
                println("ENV: Invalid line format: $line")
                return@forEach
            }

            val (key, value) = parts.map { it.trim() }
            val envKey = EnvKey.fromString(key)

            when {
                envKey == null -> println("ENV: Unknown key: $key")
                result.containsKey(envKey) -> println("ENV: Duplicate key: $key")
                else -> result[envKey] = value
            }
        }

        val missingKeys = EnvKey.entries - result.keys
        if (missingKeys.isNotEmpty()) {
            throw MissingEnvKeysException(missingKeys)
        }

        return result.toMap()
    }

    init {
        loadEnv()
    }
}

class EnvFileNotFoundException : Exception("ENV: .env file not found")
class MissingEnvKeyException(key: EnvKey) : Exception("ENV: Missing required key: $key")
class MissingEnvKeysException(keys: Collection<EnvKey>) :
    Exception("ENV: Missing required keys: ${keys.joinToString()}")