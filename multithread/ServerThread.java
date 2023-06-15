import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket client;

    public ServerThread(Socket client, String name){
        super(name); // apelare contructor Thread
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Client conectat. Port = " + client.getLocalSocketAddress() + " Nume: " + this.getName());

        try(DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream())){

            while(true) {
                String mesajIn = in.readUTF();
                System.out.println(this.getName() + " " + mesajIn);

                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                //String mesajOut = br.readLine();

                sleep(500);

                out.writeUTF("ECHO:  " + mesajIn) ;
            }

        } catch (EOFException e){
            System.out.println("Client + "  +  this.getName() + " " + client.getLocalSocketAddress() + " deconectat");
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
