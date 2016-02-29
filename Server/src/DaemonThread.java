import players.PlayerThread;
import table.Tables;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DaemonThread extends Thread{

    private final int port = 4000;
    private ServerSocket serverSocket = null;
    private Tables tables = null;
    private LinkedList<PlayerThread> players = new LinkedList<>();

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
                PlayerThread pt = new PlayerThread(socket,tables);
                //players.add(pt);
                pt.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void managePlayers(){          //无效啊
        while(true){
            try {
                sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Iterator<PlayerThread> it = players.iterator();
            while(it.hasNext()){
                PlayerThread p = it.next();
                if(!p.isAlive()){
                    it.remove();
                    continue;
                }
                if(p.checkLostConnect()){
                    System.out.println("检测到退出");
                    p.handPlayerQuit();
                    it.remove();
                }
            }
        }
    }

    public static void main(String[] args){
        DaemonThread daemonThread = new DaemonThread();
        daemonThread.start();
        //daemonThread.managePlayers();
    }
}
