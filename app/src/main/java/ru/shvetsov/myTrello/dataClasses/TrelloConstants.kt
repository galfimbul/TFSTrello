package ru.shvetsov.myTrello.dataClasses

/**
 * Created by Alexander Shvetsov on 27.10.2019
 */
object TrelloConstants {
    const val BASE_URL = "https://api.trello.com/"

    const val CONSUMER_KEY = "8d869c4575c458ebbd29180639fce2f9"

    const val CONSUMER_SECRET = "ef72f50d0044f87a646b70fcdc5986b7b968682c31d194f7621ae0ce0ad1644c"

    const val REST_CALLBACK_URL = "https://aeshvetsov.com"

    const val ACCESS_TOKEN_KEY = "access_token"

    const val PERSONAL_BOARDS = "Personal Boards"

    const val CARD_CHANGES_VIEW_MODEL_BOARD_FIELDS = "id,type,date,memberCreator,display"

    const val CARD_CHANGES_VIEW_MODEL_BOARD_FILTERS =
        "updateCard:idList,commentCard,addAttachmentToCard,createCard,addMemberToCard,updateCard:desc"
    const val ADD_CARD_MEMBERS_VIEW_MODEL_BOARD_FIELDS = "id,type,date,memberCreator,display"

    const val ADD_CARD_MEMBERS_MEMBERS_FIELDS = "id,avatarHash,avatarUrl,initials,fullName,username"
    const val CARD_INFO_VIEW_MODEL_FIELDS = "id,desc,name,idOrganization,idMembers"
    const val CARD_INFO_VIEW_MODEL_MEMBER_FIELDS = "id,avatarHash,avatarSource,initials,fullName,username"
    const val CARD_INFO_VIEW_MODEL_BOARD_FIELDS = "name,idOrganization"

    const val CARD_INFO_VIEW_MODEL_LIST_FIELDS = "name"
    const val INPUT_BOARD_NAME_VIEW_MODEL_FILTER = "all"
    const val INPUT_BOARD_NAME_VIEW_MODEL_FIELDS = "id,name,displayName"
    const val LIST_OF_BOARDS_VIEW_MODEL_EMPTY_LIST_ID = "0"
    const val LIST_OF_BOARDS_VIEW_MODEL_EMPTY_LIST_BOARD_NAME = "Нет данных"
    const val LIST_OF_BOARDS_VIEW_MODEL_EMPTY_LIST_CATEGORY = "Empty Data"

    const val SINGLE_BOARD_VIEW_MODEL_CARDS = "open"
    const val SINGLE_BOARD_VIEW_MODEL_CARD_FIELDS = "id,name,pos,idList,desc"
    const val SINGLE_BOARD_VIEW_MODEL_LISTS = "open"
    const val SINGLE_BOARD_VIEW_MODEL_LISTS_FIELDS = "id,name,pos"
    const val SINGLE_BOARD_VIEW_MODEL_MEMBERS_INVITED = "all"
    const val SINGLE_BOARD_VIEW_MODEL_MEMBER_FIELDS = "id,avatarHash,avatarUrl,initials,fullName,username"
    const val SINGLE_BOARD_VIEW_MODEL_CARD_MEMBER_FIELDS = "id,avatarHash,avatarUrl,initials,fullName,username"
    const val SINGLE_BOARD_VIEW_MODEL_POS = "bottom"


}