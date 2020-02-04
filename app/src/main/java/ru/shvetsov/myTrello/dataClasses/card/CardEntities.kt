package ru.shvetsov.myTrello.dataClasses.card

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
data class CardEntities(
    val attachment: EntitiesAttachment,
    val attachmentPreview: EntitiesAttachmentPreview,
    val listBefore: EntitiesList,
    val listAfter: EntitiesList,
    val member: EntitiesMember
)

data class EntitiesAttachmentPreview(
    val type: String,
    val id: String,
    val previewUrl: String?,
    val previewUrl2x: String?,
    val text: String,
    val originalUrl: String
)

data class EntitiesMember(
    val id: String,
    val username: String,
    val text: String
)


data class EntitiesList(
    val type: String,
    val id: String,
    val text: String
)

data class EntitiesAttachment(
    val type: String,
    val id: String,
    val previewUrl: String?,
    val previewUrl2x: String?,
    val text: String,
    val url: String
)
