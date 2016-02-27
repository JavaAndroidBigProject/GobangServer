import players.PlayerThread;
import table.Tables;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class DaemonThread extends Thread{

    private final int port = 4000;
    private ServerSocket serverSocket = null;
    private Tables tables = null;

    public DaemonThread(){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("服务器启动失败");
            e.printStackTrace();
        }
        System.out.println("服务器启动成功，监听" + port + "端口...");
        tables = new Tables(4);
    }

    public void run(){
        while(true){
            try {
                Socket socket = serverSocket.accept();
                new PlayerThread(socket,tables).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        DaemonThread daemonThread = new DaemonThread();
        daemonThread.start();
    }
}
