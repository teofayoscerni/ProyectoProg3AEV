package models;

import java.sql.*;
import java.util.ArrayList;

public class GestionDatos {
    //**Atributos comunes de los metodos**//
    public static Connection con;
    public static PreparedStatement ps;
    public static String sql;
    //Get-Select
    public static ArrayList<User> getUsersDB(){
        ArrayList<User> listUsersBD = new ArrayList<>();
        Connection con = ConexionBD.conect();
        if (con == null){return listUsersBD;}

        try {
            String sql = "select * from usuarios";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()){
                User u = new User(
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("contrasena"),
                        rs.getString("rol"));
                listUsersBD.add(u);
            }
            rs.close();
            stm.close();
            con.close();
        }catch (Exception e){
            System.out.println("Error al extraer los usuarios de la base de datos");
            e.printStackTrace();
        }
        return listUsersBD;
    }
    public static ArrayList<Product> getProductsDB(){
        ArrayList<Product> listProductsBD = new ArrayList<>();
        Connection con = ConexionBD.conect();
        if (con == null){return listProductsBD;}

        try {
            String sql = "select * from productos";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()){
                Product p = new Product(
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad"));
                listProductsBD.add(p);
            }
            rs.close();
            stm.close();
            con.close();
        }catch (Exception e){
            System.out.println("Error al extraer los productos de la base de datos");
            e.printStackTrace();
        }
        return listProductsBD;
    }
    //Set-Insert
    public static void insertProductDB(Product p) {
        con = ConexionBD.conect();
        if (con == null) return;

        sql = """
                insert into productos (nombre, descripcion, precio, cantidad)
                values (?,?,?,?)
                """;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.executeUpdate();
            ps.close();
            con.close();
            System.out.println("el producto se añadio de manera satisfactoria");
        }catch (SQLException e){
            System.out.println("error al insertar el producto");
            e.printStackTrace();
        }
    }
    public static void insertUserDB(User u) {
        con = ConexionBD.conect();
        if (con == null) return;

        sql = """
                insert into usuarios (nombre, email, contrasena, rol)
                values (?,?,?,?)
                """;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRol());
            ps.executeUpdate();
            ps.close();
            con.close();
            System.out.println("el usuario se añadio de manera satisfactoria");
        }catch (SQLException e){
            System.out.println("error al insertar el usuario");
            e.printStackTrace();
        }
    }
    public static void updateProductDB(Product p){
        con = ConexionBD.conect();
        if (con == null) return;

        sql = """
                update productos set descripcion = ?, precio = ?,
                 cantidad = ? where nombre = ?
                """;

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getDescription());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getQuantity());
            ps.setString(4, p.getDescription());
            ps.executeUpdate();
            ps.close();
            con.close();
        }catch (SQLException e){
            System.out.println("Hubo un error al actualizar el producto en la bese de datos");
            e.printStackTrace();
        }
    }
    public static void deleteProductDB(String name){
        con = ConexionBD.conect();
        if (con == null) return;

        sql = "delete from productos where nombre = ?";

        try {
           ps = con.prepareStatement(sql);
           ps.setString(1, name);
           ps.executeUpdate();
           ps.close();
           con.close();
        }catch (SQLException e){
            System.out.println("Hubo un error al eliminar el producto de la base de datos");
            e.printStackTrace();
        }
    }
    public static void deleteUserDB(String mail) {
        con = ConexionBD.conect();
        if (con == null) return;

        sql = "delete from usuarios where email = ?";

        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, mail);
            ps.executeUpdate();
            ps.close();
            con.close();
        }catch (SQLException e){
            System.out.println("Hubo un error al eliminar el usuario de la base de datos");
            e.printStackTrace();
        }
    }
}
