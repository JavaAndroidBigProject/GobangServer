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

    public int getId(){                                        //获取桌子ID
        return ID;
    }

    public void addPlayer(PlayerThread player){                //添加玩家
        if(player1 == null) {
            player1 = player;
            player1.setPlayerCode(1);
        }
        else {
            player2 = player;
            player2.setPlayerCode(2);
        }
    }

    public void removePlayer(int playerCode){                  //移除该玩家
        if(player1!=null && player1.getPlayerCode() == playerCode){
            player1 = null;
        }
        if(player2!=null && player2.getPlayerCode() == playerCode){
            player2 = null;
        }
    }

    public boolean isStart(){                                   //判断游戏是否开始
        return player1 != null && player1.isHandUp() && player2!=null && player2.isHandUp();
    }

    public String getOtherName(int playerCode){                 //获取对手名字
        if(playerCode == 1)
            return player2 == null ? "empty" : player2.getPlayerInfo().name;
        else
            return player1 == null ? "empty" : player1.getPlayerInfo().name;
    }

    public int getOtherScore(int playerCode){                    //获取对手分数
        if(playerCode == 1)
            return player2 == null ? -1 : player2.getPlayerInfo().score;
        else
            return player1 == null ? -1 : player1.getPlayerInfo().score;
    }

    public boolean isOtherHandUp(int playerCode){                 //对手是否举手
        if(playerCode == 1)
            return player2 == null ? false : player2.isHandUp();
        else
            return player1 == null ? false : player1.isHandUp();
    }

    public boolean isPlayerTurn(int playerCode){                  //判断是否轮到自己下棋
        Sandbox.Player next = sandBox.getNextPlayer();
        return playerCode == 1 && next == Sandbox.Player.WHITE || playerCode == 2 && next == Sandbox.Player.BLACK;
    }

    public void opponentWin(int playerCode){                      //对手赢
        if(playerCode == 1)
            player2.win();
        else
            player1.win();
    }

    public void opponentLose(int playerCode){                      //对手输
        if(playerCode == 1)
            player2.lose();
        else
            player1.lose();
    }

    public String getChessBoard(){                                  //获取棋盘数组
        int[][] matrix = sandBox.getMatrix();

        StringBuilder result = new StringBuilder("");
        for(int[] row : matrix){
            for(int i : row){
                result.append(i);
            }
        }

        return result.toString();
    }

    public void sendMessage(String message, int sendPlayerCode){         //给另一个玩家发信息
        if (player1!=null && player1.getPlayerCode() != sendPlayerCode) {
            player1.receiveMessage(message);
        }
        if (player2!=null && player2.getPlayerCode() != sendPlayerCode) {
            player2.receiveMessage(message);
        }
    }

    public TableInfo getTableInfo(){                   //获取游戏桌信息
        PlayerInfo p1 = player1 == null? new PlayerInfo("empty",-1) : player1.getPlayerInfo();
        PlayerInfo p2 = player2 == null? new PlayerInfo("empty",-1) : player2.getPlayerInfo();
        return new TableInfo(ID,p1.name,p1.score,p2.name,p2.score);
    }

    public void init(){                                   //游戏桌初始化
        if(player1 != null)
            player1.setHandUp(false);
        if(player2 != null)
            player2.setHandUp(false);
        sandBox.init();
    }

    public void clear(){                                  //游戏桌清空
        player1 = null;
        player2 = null;
        sandBox.init();
    }

    public void retract(int playerCode){                  //玩家悔棋
        if(playerCode == 2)
            sandBox.regret(Sandbox.Player.WHITE);
        else
            sandBox.regret(Sandbox.Player.BLACK);
    }

    public void move(int playerCode, int row, int col){        //玩家下子
        if(playerCode == 1){
            sandBox.move(Sandbox.Player.WHITE,row,col);
        }
        if(playerCode == 2){
            sandBox.move(Sandbox.Player.BLACK,row,col);
        }
    }

    public Result checkWin(int playerCode){                   //检测玩家是否赢
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

    public boolean isFull(){                                 //游戏桌是否已满
        return player1!=null && player2!=null;
    }
}
