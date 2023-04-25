package com.acebook

import io.github.reactivecircus.cache4k.Cache
import org.http4k.core.*
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.ktorm.database.Database

val templateRenderer = HandlebarsTemplates().HotReload("src/main/resources")
var database = Database.connect("jdbc:postgresql://localhost:5432/${Environment.databaseName()}")

// This is a in-memory cache to store associated session IDs and user IDs
// (in-memory means it is lost once the program is restarted)
val sessionCache = Cache.Builder().build<String, Int>()

fun main() {
    val contexts = RequestContexts()

    val port = Environment.port()
    val server = appHttpHandler(contexts).asServer(Undertow(port)).start()

    println("Server started on " + server.port())
}
