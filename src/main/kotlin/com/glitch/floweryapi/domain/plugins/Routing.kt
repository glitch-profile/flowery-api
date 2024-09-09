package com.glitch.floweryapi.domain.plugins

import com.glitch.floweryapi.data.datasource.ClientsDataSource
import com.glitch.floweryapi.data.datasource.EmployeesDataSource
import com.glitch.floweryapi.data.datasource.PersonsDataSource
import com.glitch.floweryapi.domain.routes.authorizationRoutes
import com.glitch.floweryapi.domain.utils.phoneverification.PhoneVerificationManager
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureRouting() {

    routing {

        staticFiles(
            remotePath = "/static",
            File("resources")
        ) // for files. Accessible via URL/static/...

        val personsDataSource by inject<PersonsDataSource>()
        val clientsDataSource by inject<ClientsDataSource>()
        val employeesDataSource by inject<EmployeesDataSource>()
        val phoneVerificationManager by inject<PhoneVerificationManager>()

        authorizationRoutes(
            personsDataSource,
            clientsDataSource,
            employeesDataSource,
            phoneVerificationManager
        )
    }

}
