import Classes.Student;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainWrite {
    private static void scriereText(List<Student> studenti) throws IOException {
        // BufferedWriter + FileWriter
        try(var fisier = new BufferedWriter(new FileWriter("./Date/fisier.txt"))){
            for(var x: studenti){
                fisier.write(x.getID() + "," + x.getName() + "," + x.getGPA());
                fisier.newLine();
            }
        }
    }

    private static void scriereBinar(List<Student> studenti) {
        // 1. DataOutputStream + FileOutputStream
        // 2. ObjectOutputStream + FileOutputStream
        try(var fisier = new DataOutputStream(new FileOutputStream("./Date/fisier_data.dat"))){
            for(var x: studenti){
                fisier.writeInt(x.getID());
                fisier.writeFloat(x.getGPA());
                fisier.writeUTF(x.getName());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void scriereCSV(List<Student> studenti){
        try(var fisier = new BufferedWriter(new FileWriter("./Date/fisier.csv"))){
            for(var x: studenti){
                fisier.write(x.getID() + ",");
                fisier.write(x.getName()+ ",");
                fisier.write(String.valueOf(x.getGPA())+ ",\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void  scriereDB(List<Student> studenti){
        try{
            // 1. CONNECT TO SQLITE DB
            Connection connection = DriverManager.getConnection("jdbc:sqlite:./Date/database.db");

            // 2. CREATE TABLES
            String drop = "DROP TABLE IF EXISTS STUDENTS";
            String create = "CREATE TABLE STUDENTS(ID NUMBER, NAME TEXT, GPA REAL)";
            Statement statement = connection.createStatement();
            statement.execute(drop);
            statement.execute(create);

            // 3. INSERT DATA
            String insert;
            for (var x: studenti) {
                insert = "INSERT INTO STUDENTS VALUES('" +
                        x.getID() + "', '" + x.getName() + "', '" + x.getGPA() + "')";
                statement.execute(insert);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void scriereJSON(List<Student> studenti) {
        // try fara resurse -> e nevoie de .close()
        try {
            FileWriter fw = new FileWriter("./Date/fisier.json");
            JSONArray array = new JSONArray(); // [ ... ]

            for (var x : studenti) {
                JSONObject object = new JSONObject(); // { ... }

                object.put("ID", x.getID());
                object.put("Name", x.getName());
                object.put("GPA", x.getGPA());

                array.put(object);
            }

            fw.write(array.toString());

            fw.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void scriereXML(List<Student> studenti) {

//        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//        Document document = documentBuilder.newDocument();
//
//        Element rootElement = document.createElement("Students");
//        document.appendChild(rootElement);
//
//        TO-DO
    }

    public static void main(String[] args) throws IOException {
        List<Student> studenti = new ArrayList<>();
        studenti.add(new Student(1, 8.45f, "Pietroiu Robert"));
        studenti.add(new Student(2, 9.25f, "Popescu Mihai"));
        studenti.add(new Student(3, 8.28f, "Tetea Ionut"));
        studenti.add(new Student(4, 7.25f, "Dorobantiu Mihaela"));
        studenti.add(new Student(5, 7.48f, "Golescu Iuliana"));

        scriereText(studenti);
        scriereBinar(studenti);
        scriereCSV(studenti);
        scriereDB(studenti);
        scriereJSON(studenti);
        scriereXML(studenti);
    }
}

// JSON
// [
//      {
//          "Label 1": value,
//          "Label 2": value
//      } ,
//      {
//          "Label 3": value,
//          "Label 4": value
//      }
// ]