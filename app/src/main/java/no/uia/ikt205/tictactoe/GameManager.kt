package no.uia.ikt205.tictactoe

import android.util.Log
import no.uia.ikt205.tictactoe.api.GameService
import no.uia.ikt205.tictactoe.api.data.Game
import no.uia.ikt205.tictactoe.api.data.GameState

typealias PollServiceCallback = (game:Game? ) -> Unit

object GameManager {

    val TAG:String = "GameManager"

    lateinit var mainActivity:MainActivity

    val StartingGameState = GameState(mutableListOf(mutableListOf(0,0,0),mutableListOf(0,0,0),mutableListOf(0,0,0)))
    var _game:Game = Game(mutableListOf(""),"",  mutableListOf(mutableListOf(0,0,0),mutableListOf(0,0,0),mutableListOf(0,0,0)))
    var _player:String? = null

    // Error or success code when creating a new game with name and game Id
    fun createGame(player:String){
        GameService.createGame(player,StartingGameState) { game: Game?, err: Int? ->
            if(err != null){
                Log.e(TAG, "Error creating game, error code: $err")
            } else {
                Log.d(TAG, "Created game with gameID: " + game!!.gameId)
                _game.players = game.players
                _game.gameId = game.gameId
                _game.state = StartingGameState.state
                _player = player
                mainActivity.beginActivity(GameActivity::class.java, true)
            }
        }
    }

    // Error or success code when a player is joining a existing game with player name and game Id
    fun joinGame(player:String, gameId:String){
        GameService.joinGame(player,gameId) { game: Game?, err: Int? ->
            if (err != null) {
                Log.e(TAG, "Error joining game, error code: $err")
            } else {
                Log.d(TAG, "Joined game: " + game!!.gameId + "\n Players: " + game.players)
                _game.players = game.players
                _game.gameId = game.gameId
                _game.state = game.state
                _player = player
                mainActivity.beginActivity(GameActivity::class.java, false)
            }
        }
    }

    // Error or success code when updating an existing game
    fun updateGame(gameId:String, gameState:GameState, callback:PollServiceCallback){
        GameService.updateGame(gameId, gameState) { game: Game?, err: Int? ->
            if (err != null) {
                Log.e(TAG, "Error updating game, error code: $err")
            } else {
                Log.d(TAG, "Updated game: " + game!!.gameId)
                callback(game)
            }
        }
    }

    fun restartGame(gameId:String, callback:PollServiceCallback){
        GameService.updateGame(gameId, StartingGameState) { game: Game?, err: Int? ->
            if (err != null) {
                Log.e(TAG, "Error restarting game, error code: $err")
            } else {
                Log.d(TAG, "Restarted game: " + game!!.gameId)
                callback(game)
            }
        }
    }

    // Error or success code when polling game from webservice.
    fun pollGame(gameId:String, callback:PollServiceCallback){
        GameService.pollGame(gameId) { game: Game?, err: Int? ->
            if (err != null) {
                Log.e(TAG, err.toString())
            } else {
                Log.d(TAG, "Polled game: " + game!!.gameId + "\n Players: " + game.players)
                _game.players = game.players
                _game.gameId = game.gameId
                _game.state = game.state
                callback(game)
            }
        }
    }

}