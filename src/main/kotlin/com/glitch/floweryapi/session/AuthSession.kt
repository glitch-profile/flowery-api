package com.glitch.floweryapi.session

import com.glitch.floweryapi.utils.EmployeeRoles
import io.ktor.server.auth.*

data class AuthSession(
    val isRegistered: Boolean = false,
    val personId: String,
    val clientId: String? = null,
    val employeeId: String? = null,
    val employeeRoles: List<EmployeeRoles> = emptyList()
): Principal {

    fun isContainsRole(roleToCheck: EmployeeRoles): Boolean {
        return employeeRoles.contains(roleToCheck)
    }

}
