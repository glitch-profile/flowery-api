package com.glitch.floweryapi.data.datasourceimpl.users

import com.glitch.floweryapi.data.datasource.PersonsDataSource
import com.glitch.floweryapi.data.exceptions.UserNotFoundException
import com.glitch.floweryapi.data.model.users.PersonModel
import com.glitch.floweryapi.domain.utils.notificationmanager.NotificationTopic
import com.glitch.floweryapi.domain.utils.notificationmanager.NotificationsTopicsCodes
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.sessions.*
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import java.io.File

class PersonsDataSourceImpl(
    db: MongoDatabase
): PersonsDataSource {

    private val persons = db.getCollection<PersonModel>("Persons")
    private val sessionStorage = directorySessionStorage(File("build/.sessions"))
//    private val sessionStorage = directorySessionStorage(File("${Paths.get("")}/sessions"))

    override suspend fun addPerson(firstName: String, lastName: String): PersonModel {
        val personModel = PersonModel(
            firstName = firstName,
            lastName = lastName
        )
        persons.insertOne(personModel)
        return personModel
    }

    override suspend fun getPersonById(personId: String): PersonModel {
        val filter = Filters.eq("_id", personId)
        return persons.find(filter).singleOrNull() ?: throw UserNotFoundException()
    }

    override suspend fun getPersonsByIds(personIds: List<String>): List<PersonModel> {
        val filter = Filters.`in`("_id", personIds)
        return persons.find(filter).toList()
    }

    override suspend fun getPersonName(personId: String): String {
        val person = getPersonById(personId)
        return person.getPersonName()
    }

    override suspend fun updateNotificationStatus(personId: String, newStatus: Boolean): Boolean {
        val filter = Filters.eq("_id", personId)
        val update = Updates.set(PersonModel::isNotificationEnabled.name, newStatus)
        val result = persons.updateOne(filter, update)
        if (result.matchedCount == 0L) throw UserNotFoundException()
        else return result.modifiedCount != 0L
    }

    override suspend fun addNewFCMToken(personId: String, token: String): Boolean {
        val filter = Filters.eq("_id", personId)
        val update = Updates.addToSet(PersonModel::fcmTokensList.name, token)
        val result = persons.updateOne(filter, update)
        if (result.matchedCount == 0L) throw UserNotFoundException()
        else return result.modifiedCount != 0L
    }

    override suspend fun removeFCMToken(personId: String, token: String): Boolean {
        val filter = Filters.eq("_id", personId)
        val update = Updates.pull(PersonModel::fcmTokensList.name, token)
        val result = persons.updateOne(filter, update)
        if (result.matchedCount == 0L) throw UserNotFoundException()
        else return result.modifiedCount != 0L
    }

    override suspend fun updateNotificationTopics(
        personId: String,
        newTopicsList: List<NotificationsTopicsCodes>
    ): Boolean {
        val convertedTopicsCodes = newTopicsList.map { it.name }
        val filter = Filters.eq("_id", personId)
        val update = Updates.set(PersonModel::notificationTopics.name, convertedTopicsCodes)
        val result = persons.updateOne(filter, update)
        if (result.matchedCount == 0L) throw UserNotFoundException()
        else return result.modifiedCount != 0L
    }

    override suspend fun getTokensForPersonsWithTopic(notificationTopic: NotificationTopic): List<List<String>> {
        val filter = Filters.and(
            Filters.eq(PersonModel::isNotificationEnabled.name, true),
            Filters.eq(PersonModel::notificationTopics.name, notificationTopic.name),
            Filters.ne(PersonModel::fcmTokensList.name, emptyList<String>())
        )
        return persons.find(filter).toList().map { it.fcmTokensList }
    }

    override suspend fun addActiveSessionId(personId: String, sessionId: String): Boolean {
        val filter = Filters.eq("_id", personId)
        val update = Updates.addToSet(PersonModel::activeSessions.name, sessionId)
        val result = persons.updateOne(filter, update)
        if (result.matchedCount == 0L) throw UserNotFoundException()
        else return result.modifiedCount != 0L
    }

    override suspend fun removeActiveSessionId(personId: String, sessionId: String): Boolean {
        kotlin.runCatching { sessionStorage.invalidate(sessionId) }
        val filter = Filters.eq("_id", personId)
        val update = Updates.pull(PersonModel::activeSessions.name, sessionId)
        val result = persons.updateOne(filter, update)
        if (result.matchedCount == 0L) throw UserNotFoundException()
        else return result.modifiedCount != 0L
    }

    override suspend fun endAllSessionsExceptCurrent(personId: String, currentSessionId: String): Boolean {
        val sessionsToDrop = getPersonById(personId).activeSessions.toMutableList().apply {
            remove(currentSessionId)
        }
        kotlin.runCatching {
            sessionsToDrop.forEach {
                sessionStorage.invalidate(it)
            }
        }
        val filter = Filters.eq("_id", personId)
        val update = Updates.set(PersonModel::activeSessions.name, currentSessionId)
        val result = persons.updateOne(filter, update)
        if (result.matchedCount == 0L) throw UserNotFoundException()
        else return result.modifiedCount != 0L
    }
}