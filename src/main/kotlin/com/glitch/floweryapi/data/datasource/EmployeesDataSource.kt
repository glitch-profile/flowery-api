package com.glitch.floweryapi.data.datasource

import com.glitch.floweryapi.data.model.users.EmployeeModel
import com.glitch.floweryapi.domain.utils.EmployeeRoles

interface EmployeesDataSource {

    suspend fun addEmployee(
        personId: String,
        login: String,
        password: String,
        roles: List<EmployeeRoles>
    ): EmployeeModel

    suspend fun getEmployeeById(employeeId: String): EmployeeModel

    suspend fun getEmployeesByIds(employeesIds: List<String>): List<EmployeeModel>

    suspend fun login(login: String, password: String): EmployeeModel

    suspend fun checkRoleById(employeeId: String, role: EmployeeRoles): Boolean

    suspend fun updateRoles(employeeId: String, newRoles: List<EmployeeRoles>): Boolean

}