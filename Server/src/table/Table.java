package table;

import GoBangSandBox.Sandbox;
import helper.Result;
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

    public void move(int playerCode, int row, int col){
        if(playerCode == 1){
            sandBox.move(Sandbox.Player.WHITE,row,col);
        }
        if(playerCode == 2){
            sandBox.move(Sandbox.Player.BLACK,row,col);
        }
    }

    public Result checkWin(int playerCode){
        Sandbox.Player player = sandBox.getWinner();
        if(player == Sandbox.Player.UNKNOWN){
            return Result.NONE;
        }else if(player == Sandbox.Player.BLACK && playerCode == 2
                || player == Sandbox.Player.WHITE && playerCode == 1){
            return Result.WIN;
        }else if(player == Sandbox.Player.NONE){
            return Result.DRAW;
        }else{
            return Result.LOSE;
        }
    }

    public boolean isFull(){
        return player1!=null && player2!=null;
    }
}
