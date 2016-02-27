package players;

import helper.Signal;
import message.User;
import login.ImplementLogin;
import regist.ImplementRegist;
import registed.ImplementIsRegist;
import table.Table;
import table.TableInfo;
import table.Tables;
import update.ImplementUpdate;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class PlayerThread extends Thread {
    private int playerCode = 0;

    private Socket socket = null;
    private Tables tables = null;
    private Table table = null;
    private PlayerInfo playerInfo = null;
    private boolean handUp = false;

    private PrintStream printStream= null;
    private Scanner in = null;
    private Handler handler = new Handler();


    public PlayerThread(Socket socket, Tables tables){
        this.tables = tables;
        this.socket = socket;
        try {
            printStream = new PrintStream(socket.getOutputStream(),true,"utf-8");
            in = new Scanner(socket.getInputStream(),"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        String commandsLine;
        try {
           while (true) {
               //socket.sendUrgentData(1);
               if((commandsLine = in.nextLine()) != null) {
                   if (!commandsLine.equals("GET_TABLES"))
                       System.out.println(commandsLine);
                   handler.handle(commandsLine);
               }
           }
           //System.out.println("玩家  " + socket.getInetAddress().toString() + " 已掉线");
       }catch (Exception e){
           // e.printStackTrace();
           System.out.println("玩家  "+socket.getInetAddress().toString()+" 已掉线");
       }finally {
           if(table !=null ) {
               if(table.isStart()) {
                   table.sendMessage(Signal.ON_GAME_OVER + "#" + false + "#" + true + "#" + true, PlayerThread.this.playerCode);
                   table.opponentWin(playerCode);
                   table.init();
                   table.sendMessage(getOpponentTableChangeString(),playerCode);
               }else{
                   table.removePlayer(playerCode);
               }
           }
            if(playerInfo != null)
                tables.removeLoginedPlayer(playerInfo.name);

           in.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
  }

    public int getPlayerCode(){
        return this.playerCode;
    }

    public void setPlayerCode(int playerCode){
        this.playerCode = playerCode;
    }

    public boolean isHandUp(){
        return this.handUp;
    }

    public void setHandUp(boolean handUp){
        this.handUp = handUp;
    }

    public boolean isBlack(){
        return playerCode == 2;
    }

    public boolean isMyTurn(){
        return table.isPlayerTurn(playerCode);
    }

    public String getOpponentName(){
        return table.getOtherName(playerCode);
    }

    public int getOpponentScore(){
        return table.getOtherScore(playerCode);
    }

    private String getMyTableChangeString(){
        return Signal.ON_TABLE_CHANGE + "#" + playerInfo.name + "#" + playerInfo.score +
                "#" + getOpponentName() + "#" + getOpponentScore() + "#" + isHandUp() +
                "#" + isOpponentHandUp() + "#" + table.isStart()+ "#" + table.getChessBoard()  +
                "#" + isBlack() + "#" + isMyTurn();
    }

    private String getOpponentTableChangeString(){
        return Signal.ON_TABLE_CHANGE + "#" + getOpponentName() + "#" + getOpponentScore()+
                "#" + playerInfo.name + "#" + playerInfo.score  + "#" + isOpponentHandUp() +
                "#" + isHandUp() + "#" + table.isStart()+ "#" + table.getChessBoard()  +
                "#" + !isBlack() + "#" + !isMyTurn();
    }

    private void saveScore(){
        new ImplementUpdate().update(new User(playerInfo.name,"",playerInfo.score));
    }

    public void win(){
        playerInfo.score += 2;
        saveScore();
    }

    public void lose(){
        if(playerInfo.score > 0)
            playerInfo.score -= 1;
        saveScore();
    }

    public boolean isOpponentHandUp(){
        return table.isOtherHandUp(playerCode);
    }

    public PlayerInfo getPlayerInfo(){
        return playerInfo;
    }

    public void receiveMessage(String message){
        printStream.println(message);
        printStream.flush();
    }

    public class Handler {

        public  void handle(String commandsLine){
            String responseString = null;
            String [] commands = commandsLine.split("#");
            switch (commands[0]){
                case "LOGIN":
                    responseString = onRequestLogin(commands[1], commands[2]);
                    break;
                case "REGISTER":
                    responseString = onRequestRegister(commands[1], commands[2]);
                    break;
                case "ENTER_TABLES":
                    responseString = onRequestEnterTables(Integer.parseInt(commands[1]));
                    break;
                case "GET_TABLES":
                    responseString = onRequestGetTables();
                    break;
                case "GIVE_UP":
                    responseString = onRequestGiveUp();
                    break;
                case "HAND_UP":
                    responseString = onRequestHandUp();
                    break;
                case "MOVE":
                    responseString = onRequestMove(Integer.parseInt(commands[1]),Integer.parseInt(commands[2]));
                    break;
                case "QUIT_TABLE":
                    responseString = onRequestQuitTable();
                    break;
                case "RESPOND_RETRACT":
                    responseString = onRequestReposedRetract(Boolean.parseBoolean(commands[1]));
                    break;
                case "RETRACT":
                    responseString = onRequestRetract();
                    break;
                case "SEND_MESSAGE":
                    String message = commands.length < 2 ? " " : commands[1];
                    responseString = onRequestSendMessage(message);
                    break;
                default:
                    break;
            }
            if(responseString != null){
                printStream.println(responseString);
                printStream.flush();
            }
        }

        private String onRequestLogin(String userName, String password){

            if(tables.isPlayerLogined(userName))
                return Signal.ON_RESPOND_LOGIN + "#" + false + "#" + 0 + "#" + "User has logined";
            boolean isLogin = false;

            String reason = null;

            ImplementLogin implementLogin = new ImplementLogin();

            int score = implementLogin.login(new User(userName,password,0));

            if(score == -1)
            {
                score = 0;
                reason = "User not found";
            }else if(score == -2){
                score = 0;
                reason = "Password wrong";
            }else{
                playerInfo = new PlayerInfo(userName,score);
                isLogin = true;
                reason = "login success";
                tables.addLoginedPlayer(userName);
            }
            return Signal.ON_RESPOND_LOGIN + "#" + isLogin + "#" + score + "#" + reason;
        }

        private String onRequestRegister(String userName, String password){
            ImplementIsRegist isRegist = new ImplementIsRegist();

            boolean isRegister = true;
            String reason = "Register successed";
            if(isRegist.isRegisted(new User(userName,password,0))){
                isRegister = false;
                reason = "Username been used";
            }else{
                new ImplementRegist().regist(new User(userName,password,0));
            }
            return Signal.ON_RESPOND_REGISTER+"#" + isRegister + "#" + reason;
        }

        private String onRequestEnterTables(int tableId){
            table = tables.getTable(tableId);
            String reason = "";
            boolean isEnter = false;
            if(table == null) {
                reason = "No table";
            }else if(table.isFull()){
                reason = "Table is full,choose another one";
            }else{
                isEnter = true;
                reason = "Enter table success";
                printStream.println(Signal.ON_RESPOND_ENTER_TABLE+"#"+ tableId + "#" + isEnter + "#" + reason);
                table.addPlayer(PlayerThread.this);
                table.sendMessage(getOpponentTableChangeString(),playerCode);
                return getMyTableChangeString();
            }

            return Signal.ON_RESPOND_ENTER_TABLE+"#"+ tableId + "#" + isEnter + "#" + reason;
        }

        private String onRequestGetTables(){
            return Signal.ON_RESPOND_GET_TABLES + "#" + TableInfo.tableInfoArrayToString(tables.getTableInfos());
        }

        private String onRequestGiveUp(){
            if(table.isStart()) {
                table.sendMessage(Signal.ON_GAME_OVER + "#" + false + "#" + true + "#" + true, playerCode);
                printStream.println(Signal.ON_GAME_OVER + "#" + false + "#" + false + "#" + false);
                printStream.flush();
                lose();
                table.opponentWin(playerCode);
                table.init();
                table.sendMessage(getOpponentTableChangeString(), playerCode);

                return getMyTableChangeString();
            }else
                return null;
        }



        private String onRequestHandUp(){
            PlayerThread.this.setHandUp(true);
            table.sendMessage(getOpponentTableChangeString(),playerCode);
            return getMyTableChangeString();

            //ON_TABLE_CHANGE#自己用户名#自己分数#对手用户名#对手分数#自己是否举手#对手是否举手#游戏是否进行中#棋盘的逻辑数组#自己是否执黑子#是否轮到自己下
        }

        private String onRequestMove(int row, int col){
            table.move(PlayerThread.this.playerCode,row,col);

            boolean isDraw = false,isWin = false,isOpponentGiveUp = false;
            switch(table.checkWin(PlayerThread.this.playerCode)){
                case WIN:
                    table.sendMessage(Signal.ON_GAME_OVER + "#" + isDraw + "#" + isWin + "#" + isOpponentGiveUp,PlayerThread.this.playerCode);
                    isWin = true;
                    printStream.println(Signal.ON_GAME_OVER + "#" + isDraw + "#" + isWin + "#" + isOpponentGiveUp);
                    win();
                    table.opponentLose(playerCode);
                    table.init();
                    table.sendMessage(getOpponentTableChangeString(),playerCode);
                    return getMyTableChangeString();

                case DRAW:
                    isDraw = true;
                    table.sendMessage(Signal.ON_GAME_OVER + "#" + isDraw + "#" + isWin + "#" + isOpponentGiveUp,PlayerThread.this.playerCode);
                    printStream.println(Signal.ON_GAME_OVER + "#" + isDraw + "#" + isWin + "#" + isOpponentGiveUp);
                    table.init();
                    table.sendMessage(getOpponentTableChangeString(),playerCode);
                    return getMyTableChangeString();
                case NONE:
                    table.sendMessage(getOpponentTableChangeString(),playerCode);
                    return getMyTableChangeString();
                case LOSE:
                    break;
            }
            return null;
        }

        private String onRequestQuitTable(){

            if(!table.isStart()) {

                table.init();
                table.sendMessage(Signal.ON_TABLE_CHANGE + "#" + getOpponentName() + "#" + getOpponentScore()+
                        "#" + "empty" + "#" + -1  + "#" + isOpponentHandUp() +
                        "#" + isHandUp() + "#" + table.isStart()+ "#" + table.getChessBoard()  +
                        "#" + !isBlack() + "#" + !isMyTurn(),playerCode);
                table.removePlayer(playerCode);
                table = null;
                PlayerThread.this.setHandUp(false);
            }else{

                table.init();
                table.sendMessage(Signal.ON_GAME_OVER + "#" + false + "#" + true + "#" + true,playerCode);
                table.sendMessage(Signal.ON_TABLE_CHANGE + "#" + getOpponentName() + "#" + getOpponentScore()+
                        "#" + "empty" + "#" + -1  + "#" + isOpponentHandUp() +
                        "#" + isHandUp() + "#" + table.isStart()+ "#" + table.getChessBoard()  +
                        "#" + !isBlack() + "#" + !isMyTurn(),playerCode);
                lose();
                table.opponentWin(playerCode);
                table.removePlayer(PlayerThread.this.playerCode);
                table.init();
                table = null;
            }
            return null;
        }

        private String onRequestReposedRetract(boolean ifAgree){
            if(ifAgree){
                table.retract(playerCode);
                table.sendMessage(getOpponentTableChangeString(),playerCode);
                return getMyTableChangeString();
            }
            table.sendMessage(Signal.ON_RESPOND_RETRACT + "#" + ifAgree,PlayerThread.this.playerCode);
            return null;
        }

        private String onRequestRetract(){
            if(table.isStart())
                table.sendMessage(Signal.ON_OPPONENT_RETRACT, PlayerThread.this.playerCode);
            return null;
        }

        private String onRequestSendMessage(String message){
            System.out.println(message);
            table.sendMessage(Signal.ON_RECEIVE_MESSAGE + "#" + message, PlayerThread.this.playerCode);
            return null;
        }
    }

}
