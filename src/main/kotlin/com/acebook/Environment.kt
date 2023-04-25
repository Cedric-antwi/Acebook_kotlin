package com.acebook

// This object is used to centralise environment-dependent configuration
//
// Whenever some value depends of the environment the program runs in (either dev or test),
// It should be wrapped in this object to be accessed by the rest of the code.
object Environment {
    private val env = if (System.getenv("ENVIRONMENT") == "dev") "dev" else "test"

    fun databaseName() = if (env == "dev") "acebook_kotlin" else "acebook_kotlin_test"

    fun port() = if (env == "dev") 9000 else 9999
}