import models.GestionDatos;
import models.Product;
import models.User;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        String password;
        String mail;
        ArrayList<User> users = GestionDatos.getUsersDB();
        ArrayList<Product> products = GestionDatos.getProductsDB();
        int option = 0;
        boolean userExists = false;
        do {

            System.out.println("""
                    ******Bienvenido******
                    ***Que deseas hacer***
                    | 1.Registrarse      |
                    | 2.Login            |
                    """);
            option = sc.nextInt();
            sc.nextLine();
            switch (option) {

                case 1:
                    User.addUser(users, sc);
                    break;
                case 2:
                    System.out.println("····LOGIN····");
                    System.out.println("Correo: ");
                    mail = sc.nextLine();
                    System.out.println("Contraseña: ");
                    password = sc.nextLine();
                    userExists = comproveUsers(users, products, mail, password);
                    break;
                default:
                    System.out.println("introduce una opcion valida");
            }
        } while (!userExists);


    }
    public static boolean comproveUsers(ArrayList<User> users, ArrayList<Product> products, String mail, String password){
            User actualUser = null;
            for (User u : users) {
                if (u.getEmail().equals(mail) && u.getPassword().equals(password)) {
                    actualUser = u;
                    break;
                }
            }

            if (actualUser != null) {

                System.out.println("bienvenido " + actualUser.getName() +
                        " con rol de: " + actualUser.getRol());
                if (actualUser.getRol().equalsIgnoreCase("gerente")) {
                    System.out.println("**Entrendo al menu para gerentes**");
                    menuAdmin(products, users);
                    return true;
                } else {
                    System.out.println("**Entrando al menu para empleados**");
                    menuEmployee(products);
                    return true;
                }
            } else {
                System.out.println("Credenciales incorrectas");
                return false;
            }
    }
    public static void menuEmployee(ArrayList<Product> products){
        int option;

        do {
            menuE();
            System.out.println();
            option = sc.nextInt();
            sc.nextLine();

            switch (option){
                case 1:
                    Product.addProduct(products, sc);
                    break;
                case 2:
                    Product.showProducts(products);
                    break;
                case 3:
                    Product.sellProduct(products, sc);
                    break;
                case 4:
                    Product.restockProduct(products, sc);
                    break;
                case 5:
                    Product.modProduct(products, sc);
                    break;
                case 6:
                    Product.deleteProduct(products, sc);
                    break;
                case 7:
                    System.out.println("Saliendo del menú");
                    break;
                default:
                    System.out.println("introduce una opcion valida");
            }
        }while (option != 7);
    }

    private static void menuE() {
        System.out.print("""
                ++++++MENÚ EMPLEADO++++++
                |  1.Añadir producto    |
                |  2.Mostrar productos  |
                |  3.Vender producto    |
                |  4.Reponer producto   |
                |  5.Modificar producto |
                |  6.Eliminar producto  |
                |  7.Salir              |
                +++++++++++++++++++++++++
                """);
    }
    private static void menuAd() {
        System.out.println("""
            ++++++++++++Bienvenido al menu para administradores++++++++++++
            ===============================================================
            /  ++++++MENÚ EMPLEADO++++++    ++++++++MENÚ GERENTE++++++++  /
            /  |  1.Añadir producto    |    |  8.Mostrar usuarios      |  /
            /  |  2.Mostrar productos  |    |  9.Agregar usuario       |  /
            /  |  3.Vender producto    |    |  10.Eliminar usuario     |  /
            /  |  4.Reponer producto   |    |  11.Exportar productos   |  /
            /  |  5.Modificar producto |    |  12.Importar productos   |  /
            /  |  6.Eliminar producto  |    |                          |  /
            /  |  7.Salir              |    |                          |  /
            /  +++++++++++++++++++++++++    ++++++++++++++++++++++++++++  /
            ===============================================================
        """);
    }

    public static void menuAdmin(ArrayList<Product> products, ArrayList<User> users){
        int option;

        do {
            menuAd();
            option = sc.nextInt();
            sc.nextLine();

            switch (option){
                case 1:
                    Product.addProduct(products, sc);
                    break;
                case 2:
                    Product.showProducts(products);
                    break;
                case 3:
                    Product.sellProduct(products, sc);
                    break;
                case 4:
                    Product.restockProduct(products, sc);
                    break;
                case 5:
                    Product.modProduct(products, sc);
                    break;
                case 6:
                    Product.deleteProduct(products, sc);
                    break;
                case 7:
                    System.out.println("Saliendo del menú");
                    break;
                case 8:
                    User.showUsers(users);
                    break;
                case 9:
                    User.addUser(users, sc);
                    break;
                case 10:
                    User.deleteUser(users,sc);
                    break;
                case 11:
                    Product.exportProducts(products);
                    break;
                case 12:
                    Product.importProducts(products);
                    break;
                default:
                    System.out.println("introduce una opcion valida");
            }
        }while (option != 7);
    }


}