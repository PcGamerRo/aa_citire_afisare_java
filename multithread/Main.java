import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        final int PORT_NUMEBER = 8193;
        try(ServerSocket server = new ServerSocket(PORT_NUMEBER)){
            System.out.println("Se asteapta conexiuni...");

            int i=1;
            while(true){
                String name = "ServerThread" + i;
                i++;
                new ServerThread(server.accept(), name).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
