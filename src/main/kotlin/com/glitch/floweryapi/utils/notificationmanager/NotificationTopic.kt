package com.glitch.floweryapi.utils.notificationmanager

import com.glitch.floweryapi.utils.EmployeeRoles
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

enum class NotificationsTopicsCodes {
    NEW_CHATS,
    NEW_ORDERS,
    GOODS_AVAILABILITY,
    ORDERS_STATUS_UPDATES
}

@Serializable
data class NotificationTopic(
    val topicCode: NotificationsTopicsCodes,
    val name: String,
    val nameEn: String,
    val description: String,
    val descriptionEn: String
) {

    companion object {
        fun getEmployeeTopics(employeeRoles: EmployeeRoles): List<NotificationTopic> {
            return emptyList()
            // TODO
        }

        fun getClientTopics(): List<NotificationTopic> {
            return emptyList()
            // TODO
        }
    }

}





