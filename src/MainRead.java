import Classes.Student;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainRead {

    private static List<Student> citireText() throws IOException {
        List<Student> studenti = new ArrayList<>();

        try(var fisier = new BufferedReader(new FileReader("./Date/fisier.txt"))){
            studenti = fisier.lines()
                    .map(
                            e -> new Student(
                                    Integer.parseInt(e.split(",")[0]),
                                    Float.parseFloat(e.split(",")[2]),
                                            e.split(",")[1]
                            )
                    ).collect(Collectors.toList());
        }

        return studenti;
    }

    private static List<Student> citireBinar() throws IOException {
        List<Student> studenti = new ArrayList<>();

        try(var fisier = new DataInputStream(new FileInputStream("./Date/fisier_data.dat"))){
            while (fisier.available() > 0){
                int ID = fisier.readInt();
                float GPA = fisier.readFloat();
                String name = fisier.readUTF();
                Student student = new Student(ID, GPA, name);

                studenti.add(student);
            }
        }

        return studenti;
    }

    private static List<Student> citireCSV() throws IOException {
        List<Student> studenti = new ArrayList<>();

        try(var fisier = new BufferedReader(new FileReader("./Date/fisier.csv"))) {
            studenti = fisier.lines()
                    .map(
                            e -> new Student(
                                    Integer.parseInt(e.split(",")[0]),
                                    Float.parseFloat(e.split(",")[2]),
                                    e.split(",")[1]
                            )
                    )
                    .collect(Collectors.toList());
        }

        return studenti;
    }

    private static List<Student> citireDB() throws SQLException {
        List<Student> studenti = new ArrayList<>();
        // 1. Conectare DB
        Connection connection = DriverManager.getConnection("jdbc:sqlite:./Date/database.db");

        // 2. Preluare date
        String querry = "SELECT * FROM STUDENTS";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(querry);

        while(resultSet.next()){
            int ID = resultSet.getInt(1);
            float GPA = resultSet.getFloat(2);
            String name =resultSet.getString(3);
            Student student = new Student(ID, GPA, name);
            studenti.add(student);
        }
        return studenti;
    }

    private static List<Student> citireJSON() throws FileNotFoundException {
        List<Student> studenti = new ArrayList<>();

        try{
            FileReader fr = new FileReader("./Date/fisier.json");
            JSONTokener tokener = new JSONTokener(fr);
            JSONArray array = new JSONArray(tokener);

            for(int i=0;i<array.length();i++){
                var stud = array.getJSONObject(i);

                Student student = new Student(
                    stud.getInt("ID"), stud.getFloat("GPA"), stud.getString("Name")
                );

                studenti.add(student);
            }
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return studenti;
    }

    public static void main(String[] args) throws IOException, SQLException {
        List<Student> studenti = new ArrayList<>();
        studenti = citireText();
//        studenti = citireBinar();
//        studenti = citireCSV();
//        studenti = citireDB();
//        studenti = citireJSON();
//        citireXML(); TO-DO

        for(var x: studenti)
            System.out.println(x.toString());
    }
}
