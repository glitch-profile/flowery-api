package com.glitch.floweryapi.domain.routes

import com.glitch.floweryapi.data.datasource.ClientsDataSource
import com.glitch.floweryapi.data.datasource.EmployeesDataSource
import com.glitch.floweryapi.data.datasource.PersonsDataSource
import com.glitch.floweryapi.data.exceptions.UserNotFoundException
import com.glitch.floweryapi.data.model.ApiResponse
import com.glitch.floweryapi.data.model.auth.AuthAdminIncomingModel
import com.glitch.floweryapi.data.model.auth.AuthPhoneIncomingModel
import com.glitch.floweryapi.data.model.auth.AuthResponseModel
import com.glitch.floweryapi.domain.session.AuthSession
import com.glitch.floweryapi.domain.utils.EmployeeRoles
import com.glitch.floweryapi.domain.utils.phoneverification.PhoneNotFoundException
import com.glitch.floweryapi.domain.utils.phoneverification.PhoneVerificationManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

private const val PATH = "/apiV1/auth"

fun Routing.authorizationRoutes(
    persons: PersonsDataSource,
    clients: ClientsDataSource,
    employees: EmployeesDataSource,
    phoneVerificationManager: PhoneVerificationManager
) {

    post("$PATH/login-admin") {
        val loginInfo = call.receiveNullable<AuthAdminIncomingModel>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        try {
            val employee = employees.login(loginInfo.username, loginInfo.password)
            call.sessions.set(
                AuthSession(
                    personId = employee.personId,
                    employeeId = employee.id,
                    employeeRoles = employee.roles.map { EmployeeRoles.valueOf(it) }
                )
            )
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = true,
                    message = "logged in. Please register the session."
                )
            )
        } catch (e: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    post("$PATH/login-client") {
        val loginInfo = call.receiveNullable<AuthPhoneIncomingModel>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        try {
            clients.getClientByPhoneNumber(loginInfo.phone)
            phoneVerificationManager.generateVerificationCode(loginInfo.phone)
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = true,
                    message = "user found. Enter verification code."
                )
            )
        } catch (e: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound)
        }

    }

    post("$PATH/client-verification") {
        val loginInfo = call.receiveNullable<AuthPhoneIncomingModel>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        if (loginInfo.code.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        try {
            val result = phoneVerificationManager.checkVerificationCode(
                phone = loginInfo.phone,
                code = loginInfo.code
            )
            if (result) {
                val client = clients.getClientByPhoneNumber(loginInfo.phone)
                call.sessions.set(
                    AuthSession(
                        personId = client.personId,
                        clientId = client.id
                    )
                )
                call.respond(
                    ApiResponse(
                        data = Unit,
                        status = true,
                        message = "logged in. Please register the session."
                    )
                )
            } else {
                call.respond(
                    ApiResponse(
                        data = Unit,
                        status = false,
                        message = "this code is incorrect."
                    )
                )
            }
        } catch (e: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound)
        } catch (e: PhoneNotFoundException) {
            call.respond(HttpStatusCode.Conflict)
        }
    }

    post("$PATH/login-guest") {
        call.sessions.set(AuthSession(personId = "0"))
    }

    authenticate {

        post("$PATH/session-verification") {
            val currentSession = call.sessions.get<AuthSession>() ?: kotlin.run {
                call.respond(
                    ApiResponse(
                        status = false,
                        message = "unable to get session. Please login again.",
                        data = Unit
                    )
                )
                return@post
            }
            val sessionId = call.sessionId<AuthSession>()!!
            persons.addActiveSessionId(currentSession.personId, sessionId)
            call.sessions.set(currentSession.copy(isRegistered = true))
            call.respond(
                ApiResponse(
                    status = true,
                    message = "session registered.",
                    data = AuthResponseModel(
                        personId = currentSession.personId,
                        clientId = currentSession.clientId,
                        employeeId = currentSession.employeeId,
                        employeeRoles = currentSession.employeeRoles.map { it.name }
                    )
                )
            )
        }

        post("$PATH/logout") {
            val currentSession = call.sessions.get<AuthSession>()!!
            val currentSessionId = call.sessionId<AuthSession>()!!
            if (currentSession.personId != "0") {
                persons.removeActiveSessionId(currentSession.personId, currentSessionId)
            }
            call.sessions.clear<AuthSession>()
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = true,
                    message = "logged out."
                )
            )
        }
    }

//    get("$PATH/test") {
//        val sessionId = generateSessionId()
//        call.sessions.set(
//            value = AuthSession(
//                personId = "0"
//            )
//        )
//        call.respondText(sessionId)
//    }

}