package players;

import helper.Signal;
import table.Table;
import table.TableInfo;
import table.Tables;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class PlayerThread extends Thread {
    private int playerCode;

    private Socket socket = null;
    private Tables tables = null;
    private Table table = null;
    private PlayerInfo playerInfo = null;

    private PrintStream printStream= null;
    private Scanner in = null;
    private Handler handler = new Handler();


    public PlayerThread(Socket socket, Tables tables){
        this.tables = tables;
        this.socket = socket;
        try {
            printStream = new PrintStream(socket.getOutputStream());
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        String commandsLine = null;
        while((commandsLine = in.nextLine())!=null){
            System.out.println(commandsLine);
            handler.handle(commandsLine);
        }
    }

    public int getPlayerCode(){
        return this.playerCode;
    }

    public void setPlayerCode(int playerCode){
        this.playerCode = playerCode;
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
                    responseString = onRequestMove();
                    break;
                case "QUIT_TABLE":
                    responseString = onRequestQuitTable();
                    break;
                case "RESPOND_RETRACT":
                    responseString = onRequestReposeRetract(Boolean.parseBoolean(commands[1]));
                    break;
                case "RETRACT":
                    responseString = onRequestRetract();
                    break;
                case "SEND_MESSAGE":
                    responseString = onRequestSendMessage(commands[1]);
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

            return Signal.ON_RESPOND_LOGIN + "#是否登陆成功#玩家的分数#登录失败的原因";
        }

        private String onRequestRegister(String userName, String password){

            return Signal.ON_RESPOND_REGISTER+"#是否注册成功#注册失败的原因";
        }

        private String onRequestEnterTables(int tableId){
            table = tables.getTable(tableId);
            String reason = "";
            boolean isEnter = false;
            if(table == null) {
                reason = "没有此游戏桌";
            }else if(table.isFull()){
                reason = "人数已满，请选择其它游戏桌";
            }else{
                isEnter = true;
                reason = "成功进入游戏桌";
                table.addPlayer(PlayerThread.this);
            }

            return Signal.ON_RESPOND_ENTER_TABLE+"#"+ tableId + "#" + isEnter + "#" + reason;
        }

        private String onRequestGetTables(){
            return Signal.ON_RESPOND_GET_TABLES + "#" + TableInfo.tableInfoArrayToString(tables.getTableInfos());
        }

        private String onRequestGiveUp(){

            return Signal.ON_GAME_OVER+"#是否是平局#是否是自己赢#是否是某一方认输";
        }

        private String onRequestHandUp(){
            return Signal.ON_TABLE_CHANGE+"handleup";
        }

        private String onRequestMove(){
            return "move";
        }

        private String onRequestQuitTable(){
            return Signal.ON_RESPOND_QUIT_TABLE+"#退出游戏桌是否成功";
        }

        private String onRequestReposeRetract(boolean ifAgree){
            return Signal.ON_RESPOND_RETRACT+"#ifAgree";
        }

        private String onRequestRetract(){
            return Signal.ON_OPPONENT_RETRACT;
        }

        private String onRequestSendMessage(String message){
            System.out.println(message);
            table.sendMessage(Signal.ON_RECEIVE_MESSAGE + "#" + message, PlayerThread.this.playerCode);
            return null;
        }
    }

}
