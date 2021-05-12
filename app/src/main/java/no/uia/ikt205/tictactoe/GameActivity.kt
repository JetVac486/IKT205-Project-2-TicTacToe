package no.uia.ikt205.tictactoe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_game.*
import no.uia.ikt205.tictactoe.api.data.Game
import no.uia.ikt205.tictactoe.api.data.GameState
import no.uia.ikt205.tictactoe.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    val TAG: String = "GameActivity"
    var isHost: Boolean = false
    var isPlayerTurn: Boolean = false
    var isStarted: Boolean = false

    var rowCount = mutableListOf<Int>(0, 0, 0)
    var colCount = mutableListOf<Int>(0, 0, 0)
    var diaCount = mutableListOf<Int>(0, 0, 0)
    var opsCount = mutableListOf<Int>(0, 0, 0)

    private lateinit var binding:ActivityGameBinding

    lateinit var btnList: List<List<Button>>

    private lateinit var localGame: Game

    private lateinit var mainHandler: Handler

    var player1Points = 0
    var player2Points = 0
    var activePlayer = 1

    private val updateTask = object : Runnable {
        override fun run() {
            pollUpdates()
            mainHandler.postDelayed(this, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnList = listOf(
            listOf(binding.one, binding.two, binding.three),
            listOf(binding.four, binding.five, binding.six),
            listOf(binding.seven, binding.eight, binding.nine)
        )

        isHost = intent.getBooleanExtra("isHost", false)

        localGame = GameManager._game

        mainHandler = Handler(Looper.getMainLooper())

        binding.player1.text = "%1s: X".format(localGame.players[0])

        if (isHost)
            isPlayerTurn = true

        if (localGame.players.size == 2) {
            binding.player2.text = "%1s: O".format(localGame.players[1])
            isStarted = true
        } else {
            binding.turnInd.text = "GameID: %1s".format(localGame.gameId)
            binding.player2.text = "Waiting for opponent..."
        }

        binding.playAgain.setOnClickListener {
            GameManager.restartGame(localGame.gameId) {
                if (isHost) {
                    isPlayerTurn = true
                    activePlayer = 1
                } else {
                    isPlayerTurn = false
                    activePlayer = 2
                    mainHandler.post(updateTask)
                }
                localGame = it!!
                updateBoardDisplay()
                clearWinDisplay()
                updateTurnDisplay()

                binding.playAgain.visibility = View.GONE
            }
        }

        binding.playAgain.visibility = View.GONE

        updateTurnDisplay()
        updateBoardDisplay()
        mainHandler.post(updateTask)
    }

    fun btnEvent(view: View) {
        val btnSelect = view as Button

        when (btnSelect.id) {
            binding.one.id -> {changeState(0, 0) }
            binding.two.id -> {changeState(0, 1) }
            binding.three.id -> {changeState(0, 2) }
            binding.four.id -> {changeState(1, 0) }
            binding.five.id -> {changeState(1, 1) }
            binding.six.id -> {changeState(1, 2) }
            binding.seven.id -> {changeState(2, 0) }
            binding.eight.id -> {changeState(2, 1) }
            binding.nine.id -> {changeState(2, 2) }
        }
    }

    fun pollUpdates() {
        Log.d(TAG, "Polling game...")
        GameManager.pollGame(localGame.gameId) { game: Game? ->
            when {
                // Setter spiller 1 til å vente på en motstander så lenge det bare er en spiller i spillet.
                // når en motstander(player2) blir med, blir motstanderen satt til "O" og spillet starter.
                localGame.players.size != game!!.players.size && !isStarted -> {
                    binding.player2.text = "%1s: O".format(game.players[1])

                    isStarted = true
                    localGame.players = game.players

                    updateTurnDisplay()

                    mainHandler.removeCallbacks(updateTask)
                }

                localGame.state != game.state -> {
                    isPlayerTurn = true
                    localGame.state = game.state

                    updateBoardDisplay()
                    updateTurnDisplay()

                    mainHandler.removeCallbacks(updateTask)

                    if (checkforWin() != 0) {
                        isPlayerTurn = false
                        updatePoints()
                    }
                }

                else -> {
                    localGame = game
                }
            }
        }
    }

    fun updateBoardDisplay() {
        for (i in 0..2) {
            for (j in 0..2) {
                btnList[i][j].text =
                    if (localGame.state[i][j] == 1) "X" else if (localGame.state[i][j] == 2) "O" else ""

            }
        }
    }

    fun updateTurnDisplay() {
        if (localGame.players.size == 2) {
            if (isPlayerTurn) {
                binding.turnInd.text = "Your turn!"
            } else {
                binding.turnInd.text = "Opponents turn!"
            }
        }
        if (checkforWin() != 0) {
            when {
                checkforWin() == 1 -> {
                    if (isHost) {
                        binding.turnInd.text = "You win!"
                        binding.playAgain.visibility = View.VISIBLE
                    } else {
                        binding.turnInd.text = "You lose!"
                        binding.playAgain.visibility = View.VISIBLE
                    }
                }

                checkforWin() == 2 -> {
                    if (isHost) {
                        binding.turnInd.text = "You lose!"
                        binding.playAgain.visibility = View.VISIBLE
                    } else {
                        binding.turnInd.text = "You win!"
                        binding.playAgain.visibility = View.VISIBLE
                    }
                }

                checkforWin() == 3 -> {
                    binding.turnInd.text = "Draw!"
                    binding.playAgain.visibility = View.VISIBLE
                }
            }
        }
    }

    fun changeState(x: Int, y: Int) {
        if (localGame.state[x][y] == 0 && isPlayerTurn) {
            localGame.state[x][y] = if (isHost) 1 else 2
            isPlayerTurn = false

            updateBoardDisplay()
            updateTurnDisplay()
            updatePoints()
            updateGame()
        }
    }

    fun updateGame() {
        GameManager.updateGame(localGame.gameId, GameState(localGame.state)) {
            if (checkforWin() == 0)
                mainHandler.post(updateTask)
        }
    }

    fun checkforWin(): Int {
        // Hvis ingen vinner (Draw)
        var t: Int = 0
        localGame.state.forEach {
            it.forEach {
                if (it != 0)
                    t++
            }
        }
        if (t >= 9) {
            return 3
        }

        for (p in 1..2) {

            rowCount = mutableListOf<Int>(0, 0, 0)
            colCount = mutableListOf<Int>(0, 0, 0)
            diaCount = mutableListOf<Int>(0, 0, 0)
            opsCount = mutableListOf<Int>(0, 0, 0)

            for (x in 0..2) {
                for (y in 0..2) {
                    if (localGame.state[x][y] == p) {
                        rowCount[x] += 1
                        colCount[y] += 1
                        if (x == y) {
                            diaCount[x] += 1
                        }
                        if (x + y + 1 == 3) {
                            opsCount[x] += 1
                        }
                    }
                }
            }

            // Row
            rowCount.forEachIndexed { i, it ->
                if (it == 3) {
                    displayWin(0, i)
                    return p
                }
            }


            // Column
            colCount.forEachIndexed { i, it ->
                if (it == 3) {
                    displayWin(1, i)
                    return p
                }
            }

            // Diagonal
            if (diaCount.sum() == 3) {
                displayWin(2)
                return p
            }

            // Reverse diagonal
            if (opsCount.sum() == 3) {
                displayWin(3)
                return p
            }
        }

        return 0
    }

    fun displayWin(type: Int, i: Int = 0) {
        // type: 0 = row, 1 = column, 2 = diagonal, 3 = opposite diagonal
        var color: Int = 0
        when {
            type == 0 -> {
                if (btnList[i][0].text == "X") {
                    if (isHost) {
                        color = Color.GREEN
                    } else {
                        color = Color.RED
                    }
                } else if (btnList[i][0].text == "O") {
                    if (isHost) {
                        color = Color.RED
                    } else {
                        color = Color.GREEN
                    }
                }
                for (k in 0..2)
                    btnList[i][k].setBackgroundColor(color)
            }
            type == 1 -> {
                if (btnList[0][i].text == "X") {
                    if (isHost) {
                        color = Color.GREEN
                    } else {
                        color = Color.RED
                    }
                } else if (btnList[0][i].text == "O") {
                    if (isHost) {
                        color = Color.RED
                    } else {
                        color = Color.GREEN
                    }
                }
                for (k in 0..2)
                    btnList[k][i].setBackgroundColor(color)
            }
            type == 2 -> {
                if (btnList[0][0].text == "X") {
                    if (isHost) {
                        color = Color.GREEN
                    } else {
                        color = Color.RED
                    }
                } else if (btnList[0][0].text == "O") {
                    if (isHost) {
                        color = Color.RED
                    } else {
                        color = Color.GREEN
                    }
                }
                for (k in 0..2) {
                    btnList[k][k].setBackgroundColor(color)
                }
            }
            type == 3 -> {
                if (btnList[0][2].text == "X") {
                    if (isHost) {
                        color = Color.GREEN
                    } else {
                        color = Color.RED
                    }
                } else if (btnList[0][2].text == "O") {
                    if (isHost) {
                        color = Color.RED
                    } else {
                        color = Color.GREEN

                    }
                }
                for (k in 0..2) {
                    btnList[k][2 - k].setBackgroundColor(color)
                }
            }
        }
    }


    private fun updatePoints() {
        if (checkforWin() == 1) {
            player1Points++
            player_1_score.text = "${player1Points}"
        }
        if (checkforWin() == 2) {
            player2Points++
            player_2_score.text = "${player2Points}"
        }
    }

    fun clearWinDisplay() {
        btnList.forEach {
            it.forEach {
                it.setBackgroundColor(getColor(R.color.teal_200))
            }
        }
    }

}