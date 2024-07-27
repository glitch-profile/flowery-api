package com.glitch.floweryapi.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.config.*
import org.koin.dsl.module

private val mongoUri = ApplicationConfig(null).tryGetString("storage.mongo_uri").toString()
private const val DATABASE_NAME = "FloweryDatabase"

val databaseModule = module {
    single {
        MongoClient
            .create(mongoUri)
            .getDatabase(DATABASE_NAME)
    }
}