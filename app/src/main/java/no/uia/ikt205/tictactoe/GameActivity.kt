package no.uia.ikt205.knotsandcrosses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import no.uia.ikt205.knotsandcrosses.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding:ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}