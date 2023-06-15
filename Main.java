import Classes.Candidat;
import Classes.Specializare;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static Connection conectareDB(Connection connection){
        try{
            connection = DriverManager.getConnection("jdbc:sqlite:./Date/facultate.db");
            System.out.println("Conexiune realizata!");
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Specializare> parcurgereDB(Connection connection) throws SQLException {
        List<Specializare> specializari = new ArrayList<>();


        String sqlSelect = "SELECT * FROM specializari";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlSelect);

        while(resultSet.next()){
            int cod = resultSet.getInt(1);
            String denumire = resultSet.getString(2);
            int nrLocuri = resultSet.getInt(3);
            Specializare spec = new Specializare(cod, denumire, nrLocuri);
            specializari.add(spec);
        }
        return specializari;
    }

    private static List<Candidat> citireText() throws IOException {
        List<Candidat> candidati = new ArrayList<>();
        try(var fisier = new BufferedReader(new FileReader("./Date/inscrieri.txt"))){
            candidati = fisier
                    .lines()
                    .map(e-> new Candidat(
                            Long.parseLong(e.split(",")[0]),
                            e.split(",")[1],
                            Double.parseDouble(e.split(",")[2]),
                            Integer.parseInt(e.split(",")[3])
                    )).collect(Collectors.toList());
        }
        return candidati;
    }

    // MAIN
    public static void main(String[] args) throws SQLException, IOException {
        Connection connection = null;
        connection = conectareDB(connection);
        List<Specializare> specializari = parcurgereDB(connection);

        List<Candidat> candidati = citireText();

        // cerinta 1
        System.out.println("\nCerinta 1: ");
        int nr = specializari.stream().mapToInt(e->e.getLocuri()).sum();
        System.out.println("Nr total locuri facultate: " + nr);

        // cerinta 2
        System.out.println("\nCerinta 2: ");
        var specs = candidati.stream().collect(Collectors.groupingBy(Candidat::getCod_specializare, Collectors.summarizingInt(Candidat::getCod_specializare)));
        specializari.stream().forEach(
                e-> {
                    specs.entrySet().stream().forEach(
                            f -> {
                                if (e.getCod() == f.getKey())
                                    if (e.getLocuri() - f.getValue().getCount() >= 10)
                                        System.out.println(e.getCod() + " " + e.getDenumire() + ": " +
                                                (e.getLocuri() - f.getValue().getCount()) + " locuri libere");
                            }
                    );
                }
        );

        // cerinta 3
        Map<Specializare, Map<Double, Long>> rez = new HashMap<>();

        System.out.println("\nCerinta 3: ");
        var specsMediaNotelor = candidati.stream()
                .collect(Collectors.groupingBy(Candidat::getCod_specializare,
                        Collectors.summarizingDouble(Candidat::getNota_bac)));
        specializari.stream().forEach(
                e -> {
                    specsMediaNotelor.entrySet().stream().forEach(
                            f -> {
                                if (e.getCod() == f.getKey()) {
                                    Map<Double, Long> x = new HashMap<>();
                                    x.put(f.getValue().getAverage(), f.getValue().getCount());
                                    rez.put(e, x);
                                }
                            }
                    );
                }
        );

        try(var fisier = new BufferedWriter(new FileWriter("./Date/inscrieri_specializari.json"))) {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            for (var entry : rez.entrySet()) {
                jsonObject.put("cod: ", entry.getKey().getCod());
                jsonObject.put("denumire: ", entry.getKey().getDenumire());
                for(var entry2 : entry.getValue().entrySet()) {
                    jsonObject.put("numar_inregistrari: ", entry2.getValue());
                    jsonObject.put("medie: ", entry2.getKey());
                }
                jsonArray.put(jsonObject);
            }
            fisier.write(jsonArray.toString());
        }

        //cerinta 4
        String client = "";
        System.out.println("\nCerinta 4: ");
        try(ServerSocket server = new ServerSocket(8193)){
            System.out.println("Se asteapta conexiuni...");
            Socket socket = server.accept();

            System.out.println("Conexiune realizata cu clientul" + socket.getLocalSocketAddress());
            client = socket.getLocalSocketAddress().toString();

            InputStream inputStream = socket.getInputStream();
            DataInputStream in = new DataInputStream(inputStream);

            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream out = new DataOutputStream(outputStream);

            while(true) {
                String mesaj = in.readUTF();

                int locuriTotal = 0;
                int locuriOcupate = 0;

                locuriTotal = specializari.stream()
                        .filter(e -> e.getDenumire().equals(mesaj))
                        .mapToInt(e -> e.getLocuri()).sum();

                var specs2 = candidati.stream()
                        .collect(Collectors.groupingBy(Candidat::getCod_specializare));

                for (var x : specializari) {
                    for (var y : candidati) {
                        if (x.getCod() == y.getCod_specializare())
                            if (x.getDenumire().equals(mesaj))
                                locuriOcupate++;
                    }
                }

                int locuriLibere = locuriTotal - locuriOcupate;

                System.out.println("Denumire specializare primita: " + mesaj);

                out.writeUTF(String.valueOf(locuriLibere));
            }
        }
        catch (EOFException ex){
            System.out.println("Conexiune incheiata cu clientul" + client);
        }
    }
}