package no.uia.ikt205.tictactoe.dialogs

interface GameDialogListener {
    fun onDialogCreateGame(player:String)
    fun onDialogJoinGame(player: String, gameId:String)
}
