import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MainClient {
    public static void main(String args[]){
        final int PORT_NUMEBER = 8193;
        try(Socket client = new Socket("127.0.0.1", PORT_NUMEBER)){
            System.out.println("S-a conectat la server");

            InputStream inputStream = client.getInputStream();
            DataInputStream in = new DataInputStream(inputStream);

            OutputStream outputStream = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outputStream);

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while(true){
                String mesajOut = br.readLine();

                if (mesajOut.equalsIgnoreCase("exit")){
                    client.close();
                    in.close();
                    out.close();
                    br.close();
                    System.out.println("client deconectat...");
                    System.exit(0);
                }
                else {
                    out.writeUTF(mesajOut);

                    String mesajIn = in.readUTF();
                    System.out.println("Mesaj receptionat: " + mesajIn);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
