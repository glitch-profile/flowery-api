package com.glitch.floweryapi.data.datasourceimpl.users

import com.glitch.floweryapi.data.datasource.EmployeesDataSource
import com.glitch.floweryapi.data.exceptions.UserNotFoundException
import com.glitch.floweryapi.data.model.users.EmployeeModel
import com.glitch.floweryapi.utils.EmployeeRoles
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList

class EmployeesDataSourceImpl(
    db: MongoDatabase
): EmployeesDataSource {

    private val employees = db.getCollection<EmployeeModel>("Employees")

    override suspend fun addEmployee(
        personId: String,
        login: String,
        password: String,
        roles: List<EmployeeRoles>
    ): EmployeeModel {
        val convertedRoles = roles.map { it.name }
        val employee = EmployeeModel(
            personId = personId,
            login = login,
            password = password,
            roles = convertedRoles
        )
        employees.insertOne(employee)
        return employee
    }

    override suspend fun getEmployeeById(employeeId: String): EmployeeModel {
        val filter = Filters.eq("_id", employeeId)
        return employees.find(filter).singleOrNull() ?: throw UserNotFoundException()
    }

    override suspend fun getEmployeesByIds(employeesIds: List<String>): List<EmployeeModel> {
        val filter = Filters.`in`("_id", employeesIds)
        return employees.find(filter).toList()
    }

    override suspend fun login(login: String, password: String): EmployeeModel {
        val filter = Filters.and(
            Filters.eq(EmployeeModel::login.name, login),
            Filters.eq(EmployeeModel::password.name, password)
        )
        return employees.find(filter).singleOrNull() ?: throw UserNotFoundException()
    }

    override suspend fun checkRoleById(employeeId: String, role: EmployeeRoles): Boolean {
        val filter = Filters.and(
            Filters.eq("_id", employeeId),
            Filters.eq(EmployeeModel::roles.name, role.name)
        )
        return employees.find(filter).singleOrNull() != null
    }

    override suspend fun updateRoles(employeeId: String, newRoles: List<EmployeeRoles>): Boolean {
        val convertedRoles = newRoles.map { it.name }
        val filter = Filters.eq("_id", employeeId)
        val update = Updates.set(EmployeeModel::roles.name, convertedRoles)
        val result = employees.updateOne(filter, update)
        if (result.matchedCount == 0L) throw UserNotFoundException()
        else return result.modifiedCount != 0L
    }
}