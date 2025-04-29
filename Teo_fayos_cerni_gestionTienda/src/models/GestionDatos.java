package models;


import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GestionDatos {
    //**Atributos comunes de los metodos**//
    public static Connection con;
    public static PreparedStatement ps;
    public static String sql;

    //Get-Select
    public static ArrayList<User> getUsersDB() {
        //Metodo para recoger los usuarios de la BD
        ArrayList<User> listUsersBD = new ArrayList<>();
        Connection con = ConexionBD.conect();//Inicializamos la conexion
        if (con == null) {
            return listUsersBD;//Si la conexion falla
        }

        try {
            String sql = "select * from usuarios";//Sql para recoger los usuarios
            Statement stm = con.createStatement();//Sentencia
            ResultSet rs = stm.executeQuery(sql);//Ejecucion de la query

            while (rs.next()) {//Si la query devuelve valores:
                User u = new User(
                        rs.getString("nombre"),//Seleccionamos la columna y su tipo de dato
                        rs.getString("email"),
                        rs.getString("contrasena"),
                        rs.getString("rol"));
                listUsersBD.add(u);
            }
            rs.close();
            stm.close();
            con.close();
        } catch (Exception e) {
            System.out.println("Error al extraer los usuarios de la base de datos");
            e.printStackTrace();
        }
        return listUsersBD;//Devolvemos la lista de los usuarios
    }
    public static ArrayList<Product> getProductsDB() {
        ArrayList<Product> listProductsBD = new ArrayList<>();
        Connection con = ConexionBD.conect();//Establecemos conexion a la BD
        if (con == null) {//Si falla:
            return listProductsBD;
        }

        try {
            String sql = "select * from productos";//Sql para recoger productos de la BD
            Statement stm = con.createStatement();//Preparamos la sentencia
            ResultSet rs = stm.executeQuery(sql);//Ejecutamos la sentencia

            while (rs.next()) {//Si la sentencia devuelve:
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
        } catch (Exception e) {
            System.out.println("Error al extraer los productos de la base de datos");
            e.printStackTrace();
        }
        return listProductsBD;//Devolvemos la lista con los productos de la DB
    }

    //Set-Insert
    public static void insertProductDB(Product p) {
        con = ConexionBD.conect();//Establecemos conexion
        if (con == null) return;//Si falla

        try {
            if (Product.productExists(GestionDatos.getProductsDB(), p.getName())) {//Si el producto ya existe y se esta intentando duplicar
                managementDuplicateProduct(p);
            } else {


                sql = """
                        insert into productos (nombre, descripcion, precio, cantidad)
                        values (?,?,?,?)
                        """;

                ps = con.prepareStatement(sql);//Cargamos la sentencia con el sql
                //Asignacion de las variables del sql
                ps.setString(1, p.getName());
                ps.setString(2, p.getDescription());
                ps.setDouble(3, p.getPrice());
                ps.setInt(4, p.getQuantity());
                ps.executeUpdate();//Ejecutamos la query
                ps.close();
                con.close();
                System.out.println("el producto " + "<" + p.getName() + ">" + " se añadio de manera satisfactoria");

            }
        } catch (SQLException e) {
            System.out.println("error al insertar o modificar el producto");
            e.printStackTrace();
        }
    }
    public static void insertUserDB(User u) {
        con = ConexionBD.conect();//Establecemos la conexion
        if (con == null) return;//Si falla

        sql = """
                insert into usuarios (nombre, email, contrasena, rol)
                values (?,?,?,?)
                """;
        try {
            ps = con.prepareStatement(sql);//Preparamos la sentencia sql
            //Asignamos las variables de la sentencia
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRol());
            ps.executeUpdate();//Ejecutamos la query
            ps.close();
            con.close();
            System.out.println("el usuario se añadio de manera satisfactoria");
        } catch (SQLException e) {
            System.out.println("error al insertar el usuario");
            e.printStackTrace();
        }
    }
    public static void updateProductDB(Product p) {
        con = ConexionBD.conect();//Establecemos conexion
        if (con == null) return;//Si falla

        sql = """
                update productos set descripcion = ?, precio = ?,
                 cantidad = ? where nombre = ?
                """;

        try {
            ps = con.prepareStatement(sql);//Preparamos la sentencia sql
            //Asignamos variables sql
            ps.setString(1, p.getDescription());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getQuantity());
            ps.setString(4, p.getName());
            ps.executeUpdate();//Ejecutamos la query
            ps.close();
            con.close();
        } catch (SQLException e) {
            System.out.println("Hubo un error al actualizar el producto en la bese de datos");
            e.printStackTrace();
        }
    }
    public static void deleteProductDB(String name) {
        con = ConexionBD.conect();//Preparamos la conexion
        if (con == null) return;//Si falla

        sql = "delete from productos where nombre = ?";//Elimina el producto donde el nombre = a la pasada como variable

        try {
            ps = con.prepareStatement(sql);//Preparamos la sentemcia sql
            //Asignamos variables sql
            ps.setString(1, name);
            ps.executeUpdate();//Ejecutamos la query
            ps.close();
            con.close();
        } catch (SQLException e) {
            System.out.println("Hubo un error al eliminar el producto de la base de datos");
            e.printStackTrace();
        }
    }
    public static void deleteUserDB(String mail) {
        con = ConexionBD.conect();//Preparamos la conexion
        if (con == null) return;//Si falla

        sql = "delete from usuarios where email = ?";//Elimina el usuario donde el email sea = a el pasado como variable

        try {
            ps = con.prepareStatement(sql);//Preparamos sentencia sql
            //Asignamos variable sql
            ps.setString(1, mail);
            ps.executeUpdate();//Ejecutamos sentencia
            ps.close();
            con.close();
        } catch (SQLException e) {
            System.out.println("Hubo un error al eliminar el usuario de la base de datos");
            e.printStackTrace();
        }
    }

    //Gestion de errores
    public static void managementDuplicateProduct(Product dupe) throws SQLException {
        //Metodo extra que gestiona si se intenta añadir un producto ya existente en la BD
        //Ofreciendo tres posibilidades
        Scanner sc = new Scanner(System.in);
        System.out.println("El producto <" + dupe.getName() + "> ya existe en la base de datos");
        System.out.println("¿Que deseas hacer?");
        System.out.println("""
                1·Actualizar la cantidad (sumarla)
                2·Sobreescribir los datos
                3·Cancelar""");
        int option = sc.nextInt();
        sc.nextLine();

        switch (option) {
            case 1://En el caso 1 sumara las cantidades de los produsctos duplicados
                String updateQuantity = "update productos set cantidad = cantidad + ? where nombre = ?";

                try {
                    ps = con.prepareStatement(updateQuantity);//Sentencia para sumar las cantidades en BD
                    //Asignacion de variables sql
                    ps.setInt(1, dupe.getQuantity());
                    ps.setString(2, dupe.getName());
                    ps.executeUpdate();

                    ArrayList<Product> products = GestionDatos.getProductsDB();
                    for (Product p : products){
                        if (p.getName().equalsIgnoreCase(dupe.getName())){
                            p.setQuantity(p.getQuantity() + dupe.getQuantity());
                            break;//Cambia las cantidades del producto duplicado en arraylist
                        }
                    }
                    System.out.println("Cantidad actualizada correctamente");
                } catch (SQLException e) {
                    System.out.println("No se puedo actualizar");
                    e.printStackTrace();
                }
                break;
            case 2://Sobreescribe los datos
                String overwrite = """
                        update productos set descripcion = ?, precio = ?,
                        cantidad = ? where nombre = ?
                        """;
                try {
                    ps = con.prepareStatement(overwrite);//Sentencia para sobreescribir los datos en BD
                    //Asignacion de variables sql
                    ps.setString(1, dupe.getDescription());
                    ps.setDouble(2, dupe.getPrice());
                    ps.setInt(3, dupe.getQuantity());
                    ps.setString(4, dupe.getName());
                    ps.executeUpdate();
                    ps.close();
                    con.close();
                    System.out.println("El producto se sobreescribió correctamente");
                } catch (SQLException e) {
                    System.out.println("no se puedo sobreescibir el producto");
                    e.printStackTrace();
                }
                break;
            case 3://Se descartan los cambios (Se cancela la operacion)
                System.out.println("Operacion cancelada, no se realizó ningún cambio");
                break;
            default:
                System.out.println("operacion invalida, no se realizó ningún cambio");
        }
    }
}
