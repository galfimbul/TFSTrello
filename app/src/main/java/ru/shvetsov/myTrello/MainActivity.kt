package ru.shvetsov.myTrello

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.shvetsov.myTrello.dataClasses.Board
import ru.shvetsov.myTrello.dataClasses.BoardInfo
import ru.shvetsov.myTrello.dataClasses.card.Card
import ru.shvetsov.myTrello.fragments.*
import ru.shvetsov.myTrello.interfaces.FragmentListener

class MainActivity : AppCompatActivity(), FragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("M_MainActivity", "")
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.root_layout,
                    AuthFragment.newInstance(),
                    getString(R.string.main_activity_auth_fragment_tag)
                )
                .commit()
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.root_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun clickOnBoardName(board: Board) {
        createSingleBoardFragment(board)
    }

    override fun itemIsAddedInList(board: Board) {
        createSingleBoardFragment(board)
    }

    /**
     * создаем фрагмент с пустой доской
     */
    private fun createSingleBoardFragment(board: Board) {
        val singleBoardFragment = SingleBoardFragment.newInstance(board)
        setCurrentFragment(singleBoardFragment)
    }

    override fun openCardInfo(cardId: String, boardInfo: BoardInfo) {
        val cardInfoFragment = CardInfoFragment.newInstance(cardId, boardInfo)
        setCurrentFragment(cardInfoFragment)
    }

    override fun openCardChanges(card: Card) {
        val cardChangesFragment = CardChangesFragment.newInstance(card)
        setCurrentFragment(cardChangesFragment)
    }


    override fun openAuthFragment() {
        val authFragment = AuthFragment.newInstance()
        setCurrentFragment(authFragment)
        Toast.makeText(this, "Произошла ошибка. Пожалуйста авторизуйтесь заново.", Toast.LENGTH_SHORT).show()
    }

    override fun getToken() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.root_layout,
                ListOfBoardsFragment.newInstance(),
                getString(R.string.main_activity_list_of_boards_fragment_tag)
            )
            .commit()
    }
}
