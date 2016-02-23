import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        try {
            Socket socket  = new Socket("127.0.0.1",4000);
            PrintStream ps = new PrintStream(socket.getOutputStream());
            Scanner in = new Scanner(socket.getInputStream());
            Scanner cin = new Scanner(System.in);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    while((line = in.nextLine())!=null){
                        System.out.println(line);
                    }
                }
            }
            ).start();

            while(true){
                ps.println(cin.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
