package ru.shvetsov.myTrello.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.shvetsov.myTrello.R
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CARD_CHANGES_VIEW_MODEL_BOARD_FIELDS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CARD_CHANGES_VIEW_MODEL_BOARD_FILTERS
import ru.shvetsov.myTrello.dataClasses.TrelloConstants.CONSUMER_KEY
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.dataClasses.card.CardAction
import ru.shvetsov.myTrello.extensions.humanizeDiff
import ru.shvetsov.myTrello.network.CardChangesApi
import ru.shvetsov.myTrello.utils.mappers.CardChangesUiModel
import javax.inject.Inject

/**
 * Created by Alexander Shvetsov on 28.11.2019
 */
class CardChangesViewModel @Inject constructor(val retrofit: CardChangesApi) : ViewModel() {
    lateinit var apiToken: String
    private val _actionsList = MutableLiveData<List<CardChangesUiModel>>()
    val actionsList: LiveData<List<CardChangesUiModel>>
        get() = _actionsList
    private val disposablesBag = CompositeDisposable()


    fun loadActions(card: Card) {
        val filter = CARD_CHANGES_VIEW_MODEL_BOARD_FILTERS
        val fields = CARD_CHANGES_VIEW_MODEL_BOARD_FIELDS
        val result = retrofit.getListOfActions(card.id, filter, true, fields, CONSUMER_KEY, apiToken)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _actionsList.value = mapData(it)
            }, {
                _actionsList.value = emptyList()
            })
        disposablesBag.add(result)
    }

    private fun mapData(listFromApi: List<CardAction>): List<CardChangesUiModel> {
        return listFromApi.map { action ->
            createCardChangesUiItem(action)
        }

    }

    private fun submitAvatar(action: CardAction): String {
        return action.memberCreator.avatarHash.orEmpty()
    }

    private fun createCardChangesUiItem(action: CardAction): CardChangesUiModel {
        val editorName = action.memberCreator.fullName!!
        val date = action.date.humanizeDiff()
        when (action.display.translationKey) {
            "action_move_card_from_list_to_list" -> {
                val listBeforeName = action.display.entities.listBefore.text
                val listAfterName = action.display.entities.listAfter.text
                return CardChangesUiModel(
                    editorName = editorName,
                    infoResourceId = R.string.card_changes_adapter_move_from_list_to_list,
                    dateText = date
                ).apply {
                    this.listBeforeName = listBeforeName
                    this.listAfterName = listAfterName
                    avatarHash = submitAvatar(action)
                    initials = action.memberCreator.initials
                }

            }
            "action_create_card" -> {
                return CardChangesUiModel(
                    editorName = editorName,
                    infoResourceId = R.string.card_changes_adapter_action_create_card,
                    dateText = date
                ).apply {
                    avatarHash = submitAvatar(action)
                    initials = action.memberCreator.initials
                }
            }
            "action_changed_description_of_card" -> {
                return CardChangesUiModel(
                    editorName = editorName,
                    infoResourceId = R.string.card_changes_adapter_action_change_desc,
                    dateText = date
                ).apply {
                    avatarHash = submitAvatar(action)
                    initials = action.memberCreator.initials
                }
            }
            "action_add_attachment_to_card" -> {
                if (action.display.entities.attachmentPreview.previewUrl == null) {
                    return CardChangesUiModel(
                        editorName = editorName,
                        infoResourceId = R.string.card_changes_adapter_action_add_file_attach,
                        dateText = date
                    ).apply {
                        avatarHash = submitAvatar(action)
                        initials = action.memberCreator.initials
                        fileName = action.display.entities.attachment.text
                    }
                } else {
                    return CardChangesUiModel(
                        editorName = editorName,
                        infoResourceId = R.string.card_changes_adapter_action_add_attach,
                        dateText = date
                    ).apply {
                        avatarHash = submitAvatar(action)
                        initials = action.memberCreator.initials
                        previewUrl = action.display.entities.attachment.previewUrl!!
                        hasImage = true
                    }
                }
            }
            "action_added_member_to_card" -> {
                return CardChangesUiModel(
                    editorName = editorName,
                    infoResourceId = R.string.card_changes_adapter_add_member,
                    dateText = date
                ).apply {
                    avatarHash = submitAvatar(action)
                    initials = action.memberCreator.initials
                    memberText = action.display.entities.member.text
                }
            }
            "action_member_joined_card" -> {
                return CardChangesUiModel(
                    editorName = editorName,
                    infoResourceId = R.string.card_changes_adapter_joined,
                    dateText = date
                ).apply {
                    avatarHash = submitAvatar(action)
                    initials = action.memberCreator.initials
                }
            }
            else -> error("Wrong action type")
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposablesBag.clear()
    }
}