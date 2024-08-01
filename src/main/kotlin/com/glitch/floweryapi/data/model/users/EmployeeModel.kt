package com.glitch.floweryapi.data.model.users

import com.glitch.floweryapi.utils.EmployeeRoles
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.OffsetDateTime
import java.time.ZoneId

@Serializable
data class EmployeeModel(
    @BsonId
    val id: String = ObjectId().toString(),
    val personId: String,
    val login: String,
    val password: String,
    val accountCreationDate: Long = OffsetDateTime.now(ZoneId.systemDefault()).toEpochSecond(),
    val roles: List<String>
) {

    fun checkRole(role: EmployeeRoles): Boolean {
        val employeeRoles = this.roles.map { EmployeeRoles.valueOf(it) }
        return employeeRoles.contains(role)
    }

}
