package ru.shvetsov.myTrello.utils.mappers


data class CardChangesUiModel(
    val infoResourceId: Int,
    val dateText: String,
    var hasImage: Boolean = false,
    var avatarHash: String = "",
    var initials: String = "??",
    var listBeforeName: String? = null,
    var listAfterName: String? = null,
    val editorName: String,
    var fileName: String = "",
    var previewUrl: String = "",
    var memberText: String? = null
)
