package com.glitch.floweryapi.domain.plugins

import com.glitch.floweryapi.data.datasource.ClientsDataSource
import com.glitch.floweryapi.data.datasource.EmployeesDataSource
import com.glitch.floweryapi.data.datasource.PersonsDataSource
import com.glitch.floweryapi.domain.routes.authorizationRoutes
import com.glitch.floweryapi.domain.utils.EmployeeRoles
import com.glitch.floweryapi.domain.utils.phoneverification.PhoneVerificationManager
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    routing {

        //users datasource
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

//        val scope = CoroutineScope(Dispatchers.Default + Job())
//        scope.launch {
//            val person = personsDataSource.addPerson(
//                "Алан", "Вейк"
//            )
//            val client = clientsDataSource.addClient(
//                personId = person.id,
//                "89124614342"
//            )
//        }
    }

}
