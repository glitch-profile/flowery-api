package com.glitch.floweryapi.data.model.users

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class EmployeeModel(
    @BsonId
    val id: String = ObjectId().toString(),
    val personId: String,
    val login: String,
    val password: String,
    val accountCreationDate: Long,
    val roles: List<String>
)
