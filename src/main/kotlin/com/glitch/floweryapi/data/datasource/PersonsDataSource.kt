package com.glitch.floweryapi.data.datasource

import com.glitch.floweryapi.data.model.users.PersonModel
import com.glitch.floweryapi.utils.notificationmanager.NotificationTopic
import com.glitch.floweryapi.utils.notificationmanager.NotificationsTopicsCodes

interface PersonsDataSource {

    suspend fun getPersonById(personId: String): PersonModel

    suspend fun getPersonsByIds(personIds: List<String>): List<PersonModel>

    suspend fun getPersonName(personId: String): String

    suspend fun updateNotificationStatus(personId: String, newStatus: Boolean): Boolean

    suspend fun addNewFCMToken(personId: String, token: String): Boolean

    suspend fun removeFCMToken(personId: String, token: String): Boolean

    suspend fun updateNotificationTopics(personId: String, newTopicsList: List<NotificationsTopicsCodes>): Boolean

    suspend fun getTokensForPersonsWithTopic(notificationTopic: NotificationTopic): List<List<String>>

    suspend fun addActiveSessionId(personId: String, sessionId: String): Boolean

    suspend fun removeActiveSessionId(personId: String, sessionId: String): Boolean

    suspend fun endAllSessionsExceptCurrent(personId: String, currentSessionId: String): Boolean

}