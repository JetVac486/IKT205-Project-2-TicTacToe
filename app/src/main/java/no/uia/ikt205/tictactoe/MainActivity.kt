package no.uia.ikt205.tictactoe

import android.os.Bundle
import android.util.Log
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import no.uia.ikt205.tictactoe.dialogs.CreateGameDialog
import no.uia.ikt205.tictactoe.dialogs.JoinGameDialog
import no.uia.ikt205.tictactoe.dialogs.GameDialogListener
import no.uia.ikt205.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , GameDialogListener {

    val TAG:String = "MainActivity"

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        GameManager.mainActivity = this

        binding.startGameButton.setOnClickListener {
            createNewGame()
        }
        binding.joinGameButton.setOnClickListener {
            joinGame()
        }
    }
    // When clicking on "Start" button CreateGameDialog pops up
    private fun createNewGame(){
        val dlg = CreateGameDialog()
        dlg.show(supportFragmentManager,"CreateGameDialogFragment")
    }

    // When clicking on create in the dialog box a game is to be created.
    override fun onDialogCreateGame(player: String) {
        Log.d(TAG, player)
        GameManager.createGame(player)
    }

    // When clicking on "Join" button JoinGameDialog pops up
    private fun joinGame(){
        val dlg = JoinGameDialog()
        dlg.show(supportFragmentManager,"JoinGameDialogFragment")
    }

    // When clicking on join in the dialog box the player is joining or sent to the game.
    override fun onDialogJoinGame(player: String, gameId: String) {
        Log.d(TAG, "$player $gameId")
        GameManager.joinGame(player, gameId)
    }

    // Host is player 1 and starts the game
    fun <T> beginActivity(activity: Class<T>, isHost:Boolean?){
        val intent = Intent(this, activity)

        if (isHost != null)
            intent.putExtra("isHost", isHost)

        startActivity(intent)
    }
}