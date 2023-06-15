import java.io.*;
import java.net.Socket;

public class MainClient {
    public static void main(String args[]) throws IOException {
        final int PORT_NUMBER = 8193;
        try(Socket client = new Socket("127.0.0.1", PORT_NUMBER)){
            System.out.println("Ne-am conectat la serverul cu port-ul " + client.getPort());

            OutputStream outputStream = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outputStream);

            InputStream inputStream = client.getInputStream();
            DataInputStream in = new DataInputStream(inputStream);

            while(true) {
                System.out.print("Denumire specializare: ");
                BufferedReader bw = new BufferedReader(new InputStreamReader(System.in));
                String denumire = bw.readLine();

                if (denumire.equals("exit")){
                    in.close();
                    out.close();
                    client.close();
                    System.out.println("Conexiune incheiata cu portul " + client.getPort());
                    System.exit(0);
                }

                out.writeUTF(denumire);

                String mesajPrimit = in.readUTF();
                System.out.println("Exista " + mesajPrimit + " locuri disponibile pentru aceasta specializare\n");
            }
        }
    }
}