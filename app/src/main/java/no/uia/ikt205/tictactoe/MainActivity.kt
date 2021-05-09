package no.uia.ikt205.knotsandcrosses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import no.uia.ikt205.knotsandcrosses.databinding.ActivityMainBinding
import no.uia.ikt205.tictactoe.dialogs.CreateGameDialog
import no.uia.ikt205.tictactoe.dialogs.GameDialogListener

class MainActivity : AppCompatActivity() , GameDialogListener {

    val TAG:String = "MainActivity"

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener {
            createNewGame()
        }

        binding.joinGameButton.setOnClickListener {
            joinGame()
        }

    }

    private fun createNewGame(){
        val dlg = CreateGameDialog()
        dlg.show(supportFragmentManager,"CreateGameDialogFragment")
    }

    private fun joinGame(){

    }

    override fun onDialogCreateGame(player: String) {
        Log.d(TAG,player)
    }

    override fun onDialogJoinGame(player: String, gameId: String) {
        Log.d(TAG, "$player $gameId")
    }

}