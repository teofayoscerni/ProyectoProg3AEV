package models;

import java.util.ArrayList;
import java.util.Scanner;

public class User {
//    o Nombre - String
//    o Email - String
//    o Contraseña - String
//    o Rol – String
    private String name;
    private String email;
    private String password;
    private String rol;

//---------Builder---------//
    public User() {
    }
    public User(String name, String email, String password, String rol) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

//---------Metodos---------//
//*****Atributos comunes de los metodos*****//
public static User found;

    //ToString
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
    //Resto Metodos-Funciones(ejecutables por gerentes)
    public static void addUser(ArrayList<User> users, Scanner sc) {
        //Metodo para añadir un usuario a la base de datos y al arraylist mediante
        //la funcion de insertar usuarios(GestionDatos.insertDB) y el .add para el arraylist
        System.out.println("····Registrarse····");
        System.out.println("nombre: ");
        String name = sc.nextLine();
        System.out.println("email: ");
        String mail = sc.nextLine();
        System.out.println("contraseña: ");
        String password = sc.nextLine();
        System.out.println("rol (empleado - gerente): ");
        String rol = sc.nextLine();

        User neew = new User(name,mail,password,rol);
        GestionDatos.insertUserDB(neew);//Insertamos en base de datos
        users.add(neew);//insertamos en el arraylist
    }
    public static void showUsers(ArrayList<User> users) {
        //Metodo que muestra los usuarios
        users = GestionDatos.getUsersDB();
        //Cargamos el arraylist con los datos de la DB para
        //mantener la fiabilidad de los datos
        if (users.isEmpty()){
            System.out.println("No hay usuarios");
        }else {
            for (User u : users){
                System.out.println(u);
            }
            //Metodo simple para mostrar los usuarios de la DB si hay
        }
    }
    public static User searchUser(ArrayList<User> users, String email){
        //Metodo que busca un usuario en base a su email
        for (User u : users){
            if (u.getEmail().equalsIgnoreCase(email)){
                return u;
            }
        }
        return null;
    }
    public static void deleteUser(ArrayList<User> users, Scanner sc) {
        //Metodo que mediante el metodo de buscar un usuario, si lo encuentra se eliminara
        //dicho usuario, tanto de el arrayList como de la DB.
        System.out.println("Email del usuario a eliminar: ");
        String mail = sc.nextLine();

        found = searchUser(users, mail);

        if (found != null){
            users.remove(found);
            GestionDatos.deleteUserDB(mail);
            //Llamada al metodo de eliminar un suario de la BD
            System.out.println("El usuario se eliminó de manera exitosa");
        }else {
            System.out.println("Usuario no encontrado");
        }
    }

    //Getters & Setters
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
