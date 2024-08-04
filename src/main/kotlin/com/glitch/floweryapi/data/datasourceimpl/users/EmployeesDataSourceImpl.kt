package com.glitch.floweryapi.data.datasourceimpl.users

import com.glitch.floweryapi.data.datasource.EmployeesDataSource
import com.glitch.floweryapi.data.datasource.PersonsDataSource
import com.glitch.floweryapi.data.exceptions.UserNotFoundException
import com.glitch.floweryapi.data.model.users.EmployeeModel
import com.glitch.floweryapi.domain.session.AuthSession
import com.glitch.floweryapi.domain.utils.EmployeeRoles
import com.glitch.floweryapi.domain.utils.encryptor.AESEncryptor
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.sessions.*
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import java.io.File

class EmployeesDataSourceImpl(
    db: MongoDatabase,
    private val persons: PersonsDataSource
): EmployeesDataSource {

    private val employees = db.getCollection<EmployeeModel>("Employees")
    private val sessionStorage = directorySessionStorage(File("build/.sessions"))
//    private val sessionStorage = directorySessionStorage(File("${Paths.get("")}/sessions"))

    override suspend fun addEmployee(
        personId: String,
        login: String,
        password: String,
        roles: List<EmployeeRoles>
    ): EmployeeModel {
        val convertedRoles = roles.map { it.name }
        val encryptedLogin = AESEncryptor.encrypt(login)
        val encryptedPassword = AESEncryptor.encrypt(password)
        val employee = EmployeeModel(
            personId = personId,
            login = encryptedLogin,
            password = encryptedPassword,
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
        val encryptedLogin = AESEncryptor.encrypt(login)
        val encryptedPassword = AESEncryptor.encrypt(password)
        val filter = Filters.and(
            Filters.eq(EmployeeModel::login.name, encryptedLogin),
            Filters.eq(EmployeeModel::password.name, encryptedPassword)
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
        val options = FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
        val result = employees.findOneAndUpdate(filter, update, options)
        if (result == null) throw UserNotFoundException()
        else {
            val sessionSerializer = defaultSessionSerializer<AuthSession>()
            val associatedPerson = persons.getPersonById(result.personId)
            associatedPerson.activeSessions.forEach {
                val sessionString = sessionStorage.read(it)
                val sessionData = sessionSerializer.deserialize(sessionString).copy(employeeRoles = newRoles)
                val serializedSessionString = sessionSerializer.serialize(sessionData)
                sessionStorage.write(id = it, value = serializedSessionString)
            }
            return true
        }
    }
}