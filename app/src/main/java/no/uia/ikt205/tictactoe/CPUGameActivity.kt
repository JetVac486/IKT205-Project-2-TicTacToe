package no.uia.ikt205.tictactoe

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cpugame.*
import no.uia.ikt205.tictactoe.databinding.ActivityCpugameBinding
import java.util.*
import kotlin.collections.ArrayList

class CPUGameActivity : AppCompatActivity(){

    private lateinit var binding: ActivityCpugameBinding

    lateinit var btnList: List<List<Button>>


    var playerPoints = -1
    var cpuPoints = -1

    var winner = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCpugameBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_cpugame)

        reset.setOnClickListener {
            /*btnone.text = ""
              btntwo.text = ""
              btnthree.text = ""
              btnfour.text = ""
              btnfive.text = ""
              btnsix.text = ""
              btnseven.text = ""
              btneight.text = ""
              btnnine.text = ""
              enableBoxes()*/
            val intent = Intent(this, CPUGameActivity::class.java)
            startActivity(intent)
        }

        btnList = listOf(
            listOf(binding.btnone, binding.btntwo, binding.btnthree),
            listOf(binding.btnfour, binding.btnfive, binding.btnsix),
            listOf(binding.btnseven, binding.btneight, binding.btnnine)
        )
    }

    fun buClick(view: View){
        val buSelected = view as Button
        var cellID = 0
        when(buSelected.id){
            R.id.btnone -> cellID = 1
            R.id.btntwo -> cellID = 2
            R.id.btnthree -> cellID = 3
            R.id.btnfour -> cellID = 4
            R.id.btnfive -> cellID = 5
            R.id.btnsix -> cellID = 6
            R.id.btnseven -> cellID = 7
            R.id.btneight -> cellID = 8
            R.id.btnnine -> cellID = 9
        }

        playGame(cellID,buSelected)
    }

    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()
    var activePlayer = 1
    val a = 5

    private fun playGame(cellID: Int, buSelected: Button) {
        if(activePlayer==1){
            buSelected.text = "X"
            player1.add(cellID)
            activePlayer = 2
            buSelected.setBackgroundColor(Color.parseColor("#FF03DAC5"))
            if(player1.size < a){
                AutoPlay()
            }
        }else{
            buSelected.text = "O"
            player2.add(cellID)
            activePlayer = 1
            buSelected.setBackgroundColor(Color.parseColor("#ffff00"))
        }
        buSelected.isEnabled = false;
        checkWinner()
    }

    fun checkWinner(){

        if(btnone.text == "X" && btntwo.text == "X" && btnthree.text == "X"){winner = 1}
        if(btnfour.text == "X" && btnfive.text == "X" && btnsix.text == "X"){winner = 1}
        if(btnseven.text == "X" && btneight.text == "X" && btnnine.text == "X"){winner = 1}

        if(btnone.text == "X" && btnfour.text == "X" && btnseven.text == "X"){winner = 1}
        if(btntwo.text == "X" && btnfive.text == "X" && btneight.text == "X"){winner = 1}
        if(btnthree.text == "X" && btnsix.text == "X" && btnnine.text == "X"){winner = 1}

        if(btnone.text == "X" && btnfive.text == "X" && btnnine.text == "X"){winner = 1}
        if(btnthree.text == "X" && btnfive.text == "X" && btnseven.text == "X"){winner = 1}

        if(btnone.text == "O" && btntwo.text == "O" && btnthree.text == "O"){winner = 2}
        if(btnfour.text == "O" && btnfive.text == "O" && btnsix.text == "O"){winner = 2}
        if(btnseven.text == "O" && btneight.text == "O" && btnnine.text == "O"){winner = 2}

        if(btnone.text == "O" && btnfour.text == "O" && btnseven.text == "O"){winner = 2}
        if(btntwo.text == "O" && btnfive.text == "O" && btneight.text == "O"){winner = 2}
        if(btnthree.text == "O" && btnsix.text == "O" && btnnine.text == "O"){winner = 2}

        if(btnone.text == "O" && btnfive.text == "O" && btnnine.text == "O"){winner = 2}
        if(btnthree.text == "O" && btnfive.text == "O" && btnseven.text == "O"){winner = 2}

        if (winner == 0 && player1.size + player2.size == 9){
            winindex.text = "Draw"
        }

        if (winner==1){
            winindex.text = "Player 1  win!"
            updatePoints()
            disableBoxes()
        }

        if (winner==2){
            winindex.text = "Computer  win!"
            updatePoints()
            disableBoxes()
        }
    }

    private fun AutoPlay() {
        val emptyCells=ArrayList<Int>()
        for (cellID in 1..9){
            if(!(player1.contains(cellID) || player2.contains(cellID))){
                emptyCells.add(cellID)
            }
        }

        val r = Random()
        val randIndex = r.nextInt(emptyCells.size-0)+0
        val cellID = emptyCells[randIndex]

        var buSelected: Button?

        buSelected = when(cellID){
            1-> btnone
            2-> btntwo
            3-> btnthree
            4-> btnfour
            5-> btnfive
            6-> btnseven
            7-> btnseven
            8-> btneight
            9-> btnnine
            else -> {
                btnone
            }

        }

        playGame(cellID,buSelected)
    }

    private fun disableBoxes() {
        btnone.isEnabled = false
        btntwo.isEnabled = false
        btnthree.isEnabled = false
        btnfour.isEnabled = false
        btnfive.isEnabled = false
        btnsix.isEnabled = false
        btnseven.isEnabled = false
        btneight.isEnabled = false
        btnnine.isEnabled = false
    }

    private fun enableBoxes() {
        btnone.isEnabled = true
        btntwo.isEnabled = true
        btnthree.isEnabled = true
        btnfour.isEnabled = true
        btnfive.isEnabled = true
        btnsix.isEnabled = true
        btnseven.isEnabled = true
        btneight.isEnabled = true
        btnnine.isEnabled = true
    }

    private fun updatePoints() {
        if (winner == 1) {
            playerPoints++
            playerscore.text = "${playerPoints}"
        }
        if (winner == 2) {
            cpuPoints++
            cpuscore.text = "${cpuPoints}"
        }
    }
}