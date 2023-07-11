package com.acebook

object Environment {
    private val env = if (System.getenv("ENVIRONMENT") == "dev") "dev" else "test"

    fun databaseName() = if (env == "dev") "acebook_kotlin" else "acebook_kotlin_test"

    fun port() = if (env == "dev") 9000 else 9999
}