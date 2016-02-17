package table;

import GoBangSandBox.Sandbox;
import players.PlayerInfo;
import players.PlayerThread;

public class Table implements Runnable {

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

    public void sendMessage(String message, int sendPlayerCode){
        if(player1.getPlayerCode() != sendPlayerCode){
            player1.receiveMessage(message);
        }
        if(player2.getPlayerCode() != sendPlayerCode){
            player2.receiveMessage(message);
        }
    }

    public TableInfo getTableInfo(){
        PlayerInfo p1 = player1.getPlayerInfo();
        PlayerInfo p2 = player2.getPlayerInfo();
        return new TableInfo(ID,p1.name,p1.score,p2.name,p2.score);
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

    @Override
    public void run() {

    }
}