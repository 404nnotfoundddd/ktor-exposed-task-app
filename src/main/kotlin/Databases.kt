package com.example

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import java.io.File
import kotlin.system.exitProcess


fun Application.configureDatabases() {
    Database.connect(
        url = Env.get(EnvKey.POSTGRES_URL),
        user = Env.get(EnvKey.POSTGRES_USER),
        password = Env.get(EnvKey.POSTGRES_PASSWORD),
        driver = "org.postgresql.Driver",
    )
}