package no.uia.ikt205.tictactoe

import no.uia.ikt205.tictactoe.api.GameService
import no.uia.ikt205.tictactoe.api.data.Game
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    var gameState:Game? = null
    val firstPlayer:String = "Christian"
    val secondPlayer:String = "Christian"
    val initState = listOf(listOf(0,0,0), listOf(0,0,0), listOf(0,0,0))

    @Test
    fun createGame(){
        GameService.createGame(firstPlayer,initState ){ state:Game?, err:Int? ->
            gameState = state
            assertNotNull(state)
            assertNotNull(state?.gameId)
            assertEquals(firstPlayer, state?.players?.get(0))
        }
    }


    fun JoinGame(){
        GameService.joinGame(secondPlayer, gameState.gameId) {state:Game?, err:Int? ->
            gameState = state
            assertEquals(firstPlayer, state?.players?.get(0))
        }
    }


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}