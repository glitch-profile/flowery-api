package com.glitch.floweryapi.domain.routes

import com.glitch.floweryapi.data.datasource.ClientsDataSource
import com.glitch.floweryapi.data.datasource.EmployeesDataSource
import com.glitch.floweryapi.data.datasource.PersonsDataSource
import com.glitch.floweryapi.data.exceptions.UserNotFoundException
import com.glitch.floweryapi.data.model.ApiResponse
import com.glitch.floweryapi.data.model.auth.AuthAdminIncomingModel
import com.glitch.floweryapi.data.model.auth.AuthNewUserIncomingModel
import com.glitch.floweryapi.data.model.auth.AuthPhoneIncomingModel
import com.glitch.floweryapi.data.model.auth.AuthResponseModel
import com.glitch.floweryapi.domain.session.AuthSession
import com.glitch.floweryapi.domain.utils.ApiResponseMessageCode
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
import kotlinx.coroutines.launch

private const val PATH = "/apiV1/auth"

fun Routing.authorizationRoutes(
    persons: PersonsDataSource,
    clients: ClientsDataSource,
    employees: EmployeesDataSource,
    phoneVerificationManager: PhoneVerificationManager
) {

    post("$PATH/login-password") {
        val loginInfo = call.receiveNullable<AuthAdminIncomingModel>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        try {
            val employee = employees.login(loginInfo.username, loginInfo.password)
            call.sessions.set(
                AuthSession(
                    personId = employee.personId,
                    employeeId = employee.id
                )
            )
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = true,
                    messageCode = ApiResponseMessageCode.OK,
                    message = "Logged in. Please register the session."
                )
            )
        } catch (e: UserNotFoundException) {
            ApiResponse(
                data = Unit,
                status = false,
                messageCode = ApiResponseMessageCode.AUTH_DATA_INCORRECT,
                message = "User with that login and password is not found."
            )
        }
    }

    post("$PATH/login-phone") {
        val phone = call.receiveText().take(15)
        if (!Regex("^\\+7\\d{10}\$").matches(phone)) {
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = false,
                    messageCode = ApiResponseMessageCode.PHONE_INCORRECT,
                    message = "Incorrect phone number."
                )
            )
            return@post
        }
        try {
            clients.getClientByPhoneNumber(phone)
            phoneVerificationManager.generateVerificationCode(
                phone = phone,
                isNewAccount = false
            )
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = true,
                    messageCode = ApiResponseMessageCode.OK,
                    message = "User found. Enter verification code."
                )
            )
        } catch (e: UserNotFoundException) {
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = false,
                    messageCode = ApiResponseMessageCode.USER_NOT_FOUND,
                    message = "User not found."
                )
            )
        }

    }

    post("$PATH/client-verification") {
        val loginInfo = call.receiveNullable<AuthPhoneIncomingModel>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        try {
            val result = phoneVerificationManager.checkVerificationCode(
                phone = loginInfo.phone,
                code = loginInfo.code,
                isNewAccount = false
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
                        messageCode = ApiResponseMessageCode.OK,
                        message = "Logged in. Please register the session."
                    )
                )
            } else {
                call.respond(
                    ApiResponse(
                        data = Unit,
                        status = false,
                        messageCode = ApiResponseMessageCode.CODE_INCORRECT,
                        message = "This code is incorrect."
                    )
                )
            }
        } catch (e: UserNotFoundException) {
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = false,
                    messageCode = ApiResponseMessageCode.USER_NOT_FOUND,
                    message = "User not found."
                )
            )
        } catch (e: PhoneNotFoundException) {
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = false,
                    messageCode = ApiResponseMessageCode.PHONE_NOT_FOUND,
                    message = "Phone not found. Request the code again."
                )
            )
        }
    }

    post("$PATH/register-new-phone") {
        val phone = call.receiveText().take(15)
        val isPhoneAvailable = kotlin.runCatching {
            clients.getClientByPhoneNumber(phone)
        }.exceptionOrNull() is UserNotFoundException
        if (!isPhoneAvailable) {
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = false,
                    messageCode = ApiResponseMessageCode.PHONE_ALREADY_IN_USE,
                    message = "This phone number is already in use."
                )
            )
            return@post
        }
        phoneVerificationManager.generateVerificationCode(
            phone = phone,
            isNewAccount = true
        )
        call.respond(
            ApiResponse(
                data = Unit,
                status = true,
                messageCode = ApiResponseMessageCode.OK,
                message = "Verification code generated."
            )
        )
    }

    post("$PATH/register-new-user") {
        val newUserData = call.receiveNullable<AuthNewUserIncomingModel>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val formattedUserData = newUserData.copy(
            firstName = newUserData.firstName.take(20),
            lastName = newUserData.lastName.take(20)
        )
        try {
            val isPhoneConfirmed = phoneVerificationManager.checkVerificationCode(
                phone = formattedUserData.phone,
                code = formattedUserData.verificationCode,
                isNewAccount = true
            )
            if (!isPhoneConfirmed) {
                call.respond(
                    ApiResponse(
                        data = Unit,
                        status = false,
                        messageCode = ApiResponseMessageCode.CODE_INCORRECT
                    )
                )
                return@post
            }
            val personInfo = persons.addPerson(
                firstName = formattedUserData.firstName,
                lastName = formattedUserData.lastName
            )
            val clientInfo = clients.addClient(
                personId = personInfo.id,
                phoneString = formattedUserData.phone
            )
            call.sessions.set(
                AuthSession(
                    personId = personInfo.id,
                    clientId = clientInfo.id
                )
            )
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = true,
                    messageCode = ApiResponseMessageCode.OK,
                    message = "User registered. Please register the session."
                )
            )
        } catch (e: PhoneNotFoundException) {
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = false,
                    messageCode = ApiResponseMessageCode.PHONE_NOT_FOUND,
                    message = "Phone not found. Request the code again."
                )
            )
        }
    }

//    post("$PATH/login-guest") {
//        call.sessions.set(AuthSession(personId = "0"))
//    }

//    get("$PATH/check-session") {
//        val currentSession = call.sessions.get<AuthSession>()
//        if (currentSession == null) {
//            call.respond(
//                ApiResponse(
//                    data = Unit,
//                    status = false,
//                    messageCode = ApiResponseMessageCode.SESSION_NOT_FOUND,
//                    message = "Session not found or expired."
//                )
//            )
//            return@get
//        }
//        if (!currentSession.isRegistered) {
//            call.respond(
//                ApiResponse(
//                    data = Unit,
//                    status = false,
//                    messageCode = ApiResponseMessageCode.SESSION_NOT_REGISTERED,
//                    message = "Session not registered."
//                )
//            )
//            return@get
//        }
//        call.respond(
//            ApiResponse(
//                data = Unit,
//                status = true,
//                messageCode = ApiResponseMessageCode.OK,
//                message = "Session checked."
//            )
//        )
//    }

    authenticate {

        post("$PATH/session-verification") {
            val currentSession = call.sessions.get<AuthSession>() ?: kotlin.run {
//                call.respond(
//                    ApiResponse(
//                        status = false,
//                        message = "unable to get session. Please login again.",
//                        messageCode = ApiResponseMessageCode.SESSION_NOT_FOUND,
//                        data = Unit
//                    )
//                )
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val sessionId = call.sessionId<AuthSession>()!!
            launch { persons.addActiveSessionId(currentSession.personId, sessionId) }
            var newSessionData = currentSession.copy(isRegistered = true)
            if (currentSession.employeeId != null) {
                try {
                    val employee = employees.getEmployeeById(currentSession.employeeId)
                    newSessionData = newSessionData.copy(employeeRoles = employee.roles.map { EmployeeRoles.valueOf(it) })
                } catch (_: UserNotFoundException) {  }
            }
            call.sessions.set(newSessionData)
            call.respond(
                ApiResponse(
                    status = true,
                    messageCode = ApiResponseMessageCode.OK,
                    message = "Session registered.",
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
            if (currentSession.clientId != null || currentSession.employeeId != null) {
                launch {
                    persons.removeActiveSessionId(currentSession.personId, currentSessionId)
                }
            }
            call.sessions.clear<AuthSession>()
            call.respond(
                ApiResponse(
                    data = Unit,
                    status = true,
                    messageCode = ApiResponseMessageCode.OK,
                    message = "Logged out."
                )
            )
        }
    }

    authenticate("client", "employee") {

        get("$PATH/update-auth-info") {
            val currentSession = call.sessions.get<AuthSession>()!!
            call.respond(
                ApiResponse(
                    data = AuthResponseModel(
                        personId = currentSession.personId,
                        clientId = currentSession.clientId,
                        employeeId = currentSession.employeeId,
                        employeeRoles = currentSession.employeeRoles.map { it.name }
                    ),
                    status = true,
                    messageCode = ApiResponseMessageCode.OK,
                    message = "Saved user info retrieved."
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