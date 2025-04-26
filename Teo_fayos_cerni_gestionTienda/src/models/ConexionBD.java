package models;
import java.sql.Connection;
import java.sql.DriverManager;


public class ConexionBD {
    private static final String URL ="jdbc:mysql://localhost:3306/ejercicio_evaluable";
    private static final String USER ="root";
    private static final String PASSWORD ="";

    public static Connection conect(){
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }catch (Exception e){
            System.out.println("Error al conectar a la bd");
            e.printStackTrace();
            return null;
        }
    }
}
