package no.uia.ikt205.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import no.uia.ikt205.tictactoe.api.data.Game
import no.uia.ikt205.tictactoe.api.data.GameState
import no.uia.ikt205.tictactoe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    val TAG:String = "GameActivity"
    var isHost:Boolean = false
    var isPlayerTurn:Boolean = false
    var hasStarted:Boolean = false

    private lateinit var binding:ActivityGameBinding
    lateinit var btnList:List<List<Button>>
    private lateinit var localGame:Game
    private lateinit var mainHandler:Handler

    var rowCount = mutableListOf<Int>(0,0,0)
    var colCount = mutableListOf<Int>(0,0,0)
    var diaCount = mutableListOf<Int>(0,0,0)
    var revCount = mutableListOf<Int>(0,0,0)

    private val updateTask = object : Runnable {
        override fun run(){
            pollUpdates()
            mainHandler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnList = listOf(listOf(binding.one, binding.two, binding.three), listOf(binding.four, binding.five, binding.six), listOf(binding.seven, binding.eight, binding.nine))
        isHost = intent.getBooleanExtra("isHost", false)
        localGame = GameManager._game
        mainHandler = Handler(Looper.getMainLooper())
        binding.player1.text = "%1s: X".format(localGame.players[0])

        if (isHost)
            isPlayerTurn = true

        if (localGame.players.size == 2) {
            binding.player2.text = "%1s: O".format(localGame.players[1])
            hasStarted = true
        } else {
            binding.playerturns.text = "GameID: %1s".format(localGame.gameId)
            binding.player2.text = "Waiting for opponent..."
        }
        updateTurn()
        updateBoard()
        mainHandler.post(updateTask)
    }

    fun pollUpdates(){
        Log.d(TAG, "Polling game...")
        GameManager.pollGame(localGame.gameId){game: Game? ->
            when {
                // Setter spiller 1 til å vente på en motstander så lenge det bare er en spiller i spillet.
                // når en motstander(player2) blir med, blir motstanderen satt til "O" og spillet starter.
                localGame.players.size != game!!.players.size && !hasStarted -> {
                    binding.player2.text = "%1s: O".format(game.players[1])

                    hasStarted = true
                    localGame.players = game.players

                    updateTurn()

                    mainHandler.removeCallbacks(updateTask)
                }

                localGame.state != game.state -> {
                    isPlayerTurn = true
                    localGame.state = game.state

                    updateBoard()
                    updateTurn()

                    mainHandler.removeCallbacks(updateTask)

                    if (checkforWin() != 0){
                        isPlayerTurn = false
                    }
                }

                else -> {
                    localGame = game
                }
            }
        }
    }

    fun btnEvent(view:View){
        val btnSelect = view as Button

        when(btnSelect.id){
            binding.one.id->{ changeGameState(0,0)}
            binding.two.id->{ changeGameState(0,1)}
            binding.three.id->{ changeGameState(0,2)}
            binding.four.id->{ changeGameState(1,0)}
            binding.five.id->{ changeGameState(1,1)}
            binding.six.id->{ changeGameState(1,2)}
            binding.seven.id->{ changeGameState(2,0)}
            binding.eight.id->{ changeGameState(2,1)}
            binding.nine.id->{ changeGameState(2,2)}
        }
    }

    fun updateBoard(){
        for (i in 0..2){
            for (j in 0..2){
                btnList[i][j].text = if(localGame.state[i][j] == 1) "X" else if(localGame.state[i][j] == 2) "O" else ""
            }
        }
    }

    fun updateTurn(){
        if(localGame.players.size == 2){
            if (isPlayerTurn){
                binding.playerturns.text = "Your turn!"
            } else {
                binding.playerturns.text = "Opponents turn!"
            }
        }
        if (checkforWin() != 0){
            when {
                checkforWin() == 1 -> {
                    if(isHost){
                        binding.playerturns.text = "You win!"
                    } else {
                        binding.playerturns.text = "You lose!"
                    }
                }

                checkforWin() == 2 -> {
                    if(isHost){
                        binding.playerturns.text = "You lose!"
                    } else {
                        binding.playerturns.text = "You win!"
                    }
                }

                checkforWin() == 3 -> {
                    binding.playerturns.text = "Draw!"
                }
            }
        }
    }

    fun updateGame(){
        GameManager.updateGame(localGame.gameId, GameState(localGame.state)){
            if (checkforWin() == 0)
                mainHandler.post(updateTask)
        }
    }

    fun changeGameState(x:Int, y:Int){
        if(localGame.state[x][y] == 0 && isPlayerTurn){
            localGame.state[x][y] = if(isHost) 1 else 2
            isPlayerTurn = false

            updateBoard()
            updateTurn()
            updateGame()
        }
    }

    fun checkforWin():Int{

        for (p in 1..2){

            colCount = mutableListOf<Int>(0,0,0)
            rowCount = mutableListOf<Int>(0,0,0)
            diaCount = mutableListOf<Int>(0,0,0)
            revCount = mutableListOf<Int>(0,0,0)

            for (x in 0..2){
                for (y in 0..2){
                    if (localGame.state[x][y] == p){
                        colCount[y] += 1
                        rowCount[x] += 1
                        if (x == y) {
                            diaCount[x] += 1
                        }
                        if (x + y + 1 == 3){
                            revCount[x] += 1
                        }
                    }
                }
            }

            // Column
            colCount.forEach {
                if (it == 3)
                    return p
            }

            // Row
            rowCount.forEach {
                if (it == 3)
                    return p
            }

            // Diagonal
            if(diaCount.sum() == 3){
                return p
            }

            // Reverse diagonal
            if(revCount.sum() == 3){
                return p
            }
        }

        // Hvis ingen vinner (Draw)
        var t:Int = 0
        localGame.state.forEach {
            it.forEach {
                if (it != 0)
                    t++
            }
        }
        if (t >= 9){
            return 3
        }

        return 0
    }
}