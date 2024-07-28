package com.glitch.floweryapi.data.model.users

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class PersonModel(
    @BsonId
    val id: String = ObjectId().toString(),
    val firstName: String,
    val lastName: String,
    val isNotificationEnabled: Boolean = false,
    val fcmTokensList: List<String> = emptyList(),
    val notificationTopics: List<String> = emptyList(),
    @Transient
    val activeSessions: List<String> = emptyList() // session ids, without session secret
) {
    fun getPersonName() = "${this.lastName} ${this.firstName}"
}
