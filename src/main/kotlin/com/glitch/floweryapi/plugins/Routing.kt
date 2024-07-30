package com.glitch.floweryapi.plugins

import com.glitch.floweryapi.data.datasource.ClientsDataSource
import com.glitch.floweryapi.data.datasource.PersonsDataSource
import com.glitch.floweryapi.routes.authorizationRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    routing {

        //users datasource
        val personsDataSource by inject<PersonsDataSource>()
        val clientsDataSource by inject<ClientsDataSource>()

        authorizationRoutes()
    }

}
