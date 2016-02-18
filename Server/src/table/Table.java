package table;

import GoBangSandBox.Sandbox;
import players.PlayerInfo;
import players.PlayerThread;

public class Table {

    private final int ID;

    private Sandbox sandBox;

    private PlayerThread player1 = null;
    private PlayerThread player2 = null;

    public Table(int ID){
        this.ID = ID;
        sandBox = new Sandbox();
    }

    public int getId(){
        return ID;
    }

    public void addPlayer(PlayerThread player){
        if(player1 == null) {
            player1 = player;
            player1.setPlayerCode(1);
        }
        else {
            player2 = player;
            player2.setPlayerCode(2);
        }
    }

    public void removePlayer(int playerCode){
        if(player1!=null && player1.getPlayerCode() == playerCode){
            player1 = null;
        }
        if(player2!=null && player2.getPlayerCode() == playerCode){
            player2 = null;
        }
    }

    public void sendMessage(String message, int sendPlayerCode){
        if (player1!=null && player1.getPlayerCode() != sendPlayerCode) {
            player1.receiveMessage(message);
        }
        if (player2!=null && player2.getPlayerCode() != sendPlayerCode) {
            player2.receiveMessage(message);
        }
    }

    public TableInfo getTableInfo(){
        PlayerInfo p1 = player1 == null? new PlayerInfo("empty",-1) : player1.getPlayerInfo();
        PlayerInfo p2 = player2 == null? new PlayerInfo("empty",-1) : player2.getPlayerInfo();
        return new TableInfo(ID,p1.name,p1.score,p2.name,p2.score);
    }

    public void clear(){
        player1 = null;
        player2 = null;
        sandBox.init();
    }

    public void retract(int playerCode){
        if(playerCode == 1)
            sandBox.regret(Sandbox.Player.WHITE);
        else
            sandBox.regret(Sandbox.Player.BLACK);
    }

    public void judge(){
        Sandbox.Player player = sandBox.getWinner();
        if(player == Sandbox.Player.UNKNOWN){

        }else if(player == Sandbox.Player.BLACK){

        }else if(player == Sandbox.Player.WHITE){

        }else if(player == Sandbox.Player.NONE){

        }
    }

    public boolean isFull(){
        return player1!=null && player2!=null;
    }
}
