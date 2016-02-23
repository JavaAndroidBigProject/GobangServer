package players;

import helper.Signal;
import message.User;
import table.Table;
import table.TableInfo;
import table.Tables;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import login.ImplementLogin;
import regist.ImplementRegist;
import registed.ImplementIsRegist;
import update.ImplementUpdate;

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
        try {
            while ((commandsLine = in.nextLine()) != null) {
                System.out.println(commandsLine);
                handler.handle(commandsLine);
            }
        }catch (Exception e){
            System.out.println("玩家"+socket.getInetAddress().toString()+"已掉线");
            table.removePlayer(PlayerThread.this.playerCode);
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
                    responseString = onRequestReposedRetract(Boolean.parseBoolean(commands[1]));
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
            boolean isLogin = false;

            String reason = null;

            ImplementLogin implementLogin = new ImplementLogin();

            int score = implementLogin.login(new User(userName,password,0));

            if(score == -1)
            {
                score = 0;
                reason = "玩家不存在";
            }else if(score == -2){
                score = 0;
                reason = "密码错误";
            }else{
                playerInfo = new PlayerInfo(userName,score);
                isLogin = true;
                reason = "登录成功";
            }
            return Signal.ON_RESPOND_LOGIN + "#" + isLogin + "#" + score + "#" + reason;
        }

        private String onRequestRegister(String userName, String password){
            ImplementIsRegist isRegist = new ImplementIsRegist();

            boolean isRegister = true;
            String reason = "注册成功";
            if(isRegist.isRegisted(new User(userName,password,0))){
                isRegister = false;
                reason = "玩家已存在";
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
            table.sendMessage(Signal.ON_GAME_OVER + "#" + false + "#" +true + "#" +true, PlayerThread.this.playerCode);
            return Signal.ON_GAME_OVER+"#" + false + "#" + false+ "#" + false;
        }

        private String onRequestHandUp(){
            return Signal.ON_TABLE_CHANGE+"handleup";
        }

        private String onRequestMove(){
            return "move";
        }

        private String onRequestQuitTable(){
            table.removePlayer(PlayerThread.this.playerCode);
            table = null;
            return Signal.ON_RESPOND_QUIT_TABLE+"#"+ Boolean.TRUE;
        }

        private String onRequestReposedRetract(boolean ifAgree){
            if(ifAgree){
                table.retract(PlayerThread.this.playerCode);
            }
            table.sendMessage(Signal.ON_RESPOND_RETRACT + "#" + ifAgree,PlayerThread.this.playerCode);
            return null;
        }

        private String onRequestRetract(){
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
