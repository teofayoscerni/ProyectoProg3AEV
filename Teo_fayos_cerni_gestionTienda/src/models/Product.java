package models;

import com.sun.tools.javac.Main;
import jdk.swing.interop.SwingInterOpUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Product {
//    o Nombre – String
//    o Descripción – String
//    o Precio – double
//    o Cantidad – int
    private String name;
    private String description;
    private double price;
    private int quantity;


//------------Builder-------------//
    public Product() {
    }
    public Product(String name, String description, double price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }


//------------Metodos-------------//
//*****Atributos comunes de los metodos*****//
public static Product found; //Variable auxiliar de varios metodos

    //ToString
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    //metodos (ejecutables solo por los gerentes)
    public static void exportProducts(ArrayList<Product> products) {
        //Formato para el tiempo
        DateTimeFormatter ciro = DateTimeFormatter.ofPattern("yyyy-MM-dd[HH_mm_ss]");
        //Fecha actual con el formato
        String dateTime = LocalDateTime.now().format(ciro);
        //Nombre del archivo
        String fileName = "products_" + dateTime + ".txt";

        try{
            Scanner sc = new Scanner(System.in);
            System.out.println("Introduce la ruta donde se debe guardar el archivo");
            String path = sc.nextLine();
            //Ruta del archivo + el nombre anteriormente indicado
            File file = new File(path + "\\" + fileName);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            //Esto es lo que se escribirá en el archivo
            for (Product p : products){
                bw.write(p.getName() + " |" + p.getDescription() +
                        " |" + p.getQuantity() + " |" + p.getPrice());
                bw.newLine();
            }

            bw.close();
            System.out.println("Los productos se exportaron correctamente a: " +
                    file.getAbsolutePath());


        }catch (Exception e){
            System.out.println("Algo salió mal al exportar los productos");
            e.printStackTrace();
        }
    }
    public static void importProducts(ArrayList<Product> products) {
       //Metodo para importar productos desde un txt
        //Para su correcta importacion cada linea del formato ha de tener
        //el mismo formato con el que se escribió la lista de exportacion
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Ruta completa del archivo a importar: ");
            String path = sc.nextLine();
            File file = new File(path);
            //archivo a importar

            if (!file.exists()) {
                //Buscamos si el archivo existe
                System.out.println("El archivo no existe");
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            //Le pasamos al br el archivo a leer
            String line;

            while ((line = br.readLine()) != null) {
                //El bucle se hara simepre y cuando la linea no este vacia
                //Indicamos que ha de hacer la separacion de los items por "|"
                String[] data = line.split("\\|");

                String name = data[0];
                String description = data[1];
                int quantity = Integer.parseInt(data[2].trim());//Usamos trim para quitar los espacios en blanco
                double price = Double.parseDouble(data[3].trim());
                //Inicializacion del  array para guardar los diferentes tipos de informacion
                //Creamos un producto nuevo con la informacion sacada del archivo txt que estaba
                //almacenada en el array
                Product neew = new Product(name, description, price, quantity);

                if (!productExists(products, name)) {
                    products.add(neew);
                    GestionDatos.insertProductDB(neew);
                    //Si el producto no existia ya se mete al array y a la DB
                }
            }

            br.close();
            System.out.println("Productos importados correctamente");
        }catch (Exception e){
            System.out.println("Algo salió mal al importar los productos");
            e.printStackTrace();
        }
    }


    //resto metodos-Funciones(ejecutables por gerentes y empleados)
    public static void addProduct(ArrayList<Product> products, Scanner sc) {
        //Metodo para agregar un nuevo producto al arrayLista y a la BD
        System.out.println("Nombre del producto: ");
        String name = sc.nextLine();
        System.out.println("Descripcion: ");
        String description = sc.nextLine();
        System.out.println("Precio: ");
        double price = sc.nextDouble();
        sc.nextLine();
        System.out.println("Cantidad: ");
        int quantity = sc.nextInt();
        sc.nextLine();

        Product neew = new Product(name,description,price,quantity);
        GestionDatos.insertProductDB(neew);
        products.add(neew);
    }
    public static void showProducts(ArrayList<Product> products) {
        //Metodo que recorre el array y muestra los items si los hay
        if (products.isEmpty()){
            System.out.println("no hay productos");
        }else {
            for (Product p : products){
                System.out.println(p.toString());
            }
        }
    }
    public static Product searchProduct(ArrayList<Product> products, String name){
        //Metodo que recorre el arraylist comprobando el nombre pasado como parametro
        for (Product p : products){
            if (p.getName().equalsIgnoreCase(name)){
                return p;
            }
        }
        return null;
    }
    public static void sellProduct(ArrayList<Product> products, Scanner sc) {
        //Metodo que vende un producto si hay stock suficiente
        //Actualizando los datos tanto en el arrayList como en DB
        System.out.println("nombre del producto a vender: ");
        String name = sc.nextLine();
        //Buscamos el producto deseado
        found = searchProduct(products, name);

        if (found != null){//Si se encuentra el producto
            System.out.println("cantidad a vender: ");
            int sellQuantity = sc.nextInt();
            sc.nextLine();

            if (sellQuantity > 0 && found.getQuantity() >= sellQuantity){//Si hay suficiente cantidad del producto
                found.setQuantity(found.getQuantity() - sellQuantity);
                GestionDatos.updateProductDB(found);
                System.out.println("La venta se realizo de manera exitosa");
            }else {
                System.out.println("No hay suficiente cantidad para vender");
                System.out.println("Cantidad del producto " + found.getName() +
                        ": " + found.getQuantity());
            }
        }else {
            System.out.println("No se encontro el producto " + name);
        }
    }
    public static void restockProduct(ArrayList<Product> products, Scanner sc) {
        //Metodo para reponer la cantidad de un producto, comprobando que la cantidad a reponer es > a 0
        System.out.println("Nombre del producto a reponer: ");
        String name = sc.nextLine();
        //Buscamos el producto
        found = searchProduct(products, name);

        if (found != null){//Si se encuentra
            System.out.println("Cantidad a  reponer: ");
            int restockQuantity = sc.nextInt();
            sc.nextLine();

            if (restockQuantity > 0){//Si la cantidad a reponer es > a 0
                found.setQuantity(found.getQuantity() + restockQuantity);
                GestionDatos.updateProductDB(found);
                System.out.println("Producto repuesto de manera exitosa");
            }else{
                System.out.println("La cantidad a reponer no puede ser menor que 0");
            }
        }else {
            System.out.println("No se encontró el producto con el nombre " + name);
        }
    }
    public static void modProduct(ArrayList<Product> products, Scanner sc) {
        //Metodo para modificar los atributos de un producto existente
        System.out.println("Nombre del producto a modificar: ");
        String name = sc.nextLine();
        //Buscamos el producto
        found = searchProduct(products, name);

        if (found != null){//Si se encuentra
            System.out.println("Nueva descripcion:");
            String description = sc.nextLine();
            System.out.println("Nuevo precio:");
            double price = sc.nextDouble();
            sc.nextLine();
            System.out.println("Nueva cantidad:");
            int quantity = sc.nextInt();
            sc.nextLine();
            //Asignamos sus nuevos valores
            found.setDescription(description);
            found.setPrice(price);
            found.setQuantity(quantity);
            //Actualizamos en DB
            GestionDatos.updateProductDB(found);
            System.out.println("El producto se modifico de manera exitosa");
        }else {
            System.out.println("El producto " + name + " no se encontró");
        }
    }
    public static void deleteProduct(ArrayList<Product> products, Scanner sc) {
        //Metodo para eliminar un producto buscado
        System.out.println("Nombre del producto a eliminar: ");
        String name = sc.nextLine();
        //Buscamos el producto
        found = searchProduct(products,name);

        if (found != null){//Si existe lo elimina del arrayList y la BD
            products.remove(found);
            GestionDatos.deleteProductDB(found.getName());
            System.out.println("El producto " + name + " se eliminó con exito");
        }else {
            System.out.println("No se encontró el producto " + name);
        }
    }
    public static boolean productExists(ArrayList<Product> products, String name) throws SQLException {
        //Comprobamos si el producto existe en el arraylist
        for (Product p : products){
            if (p.getName().equalsIgnoreCase(name)){
                return true;//Devuelve true si existe
            }
        }
        //Si no esta en el arraylist consultamos a la base de datos
        Connection con = ConexionBD.conect();
        if (con == null) return false;//Si la conexion falla asumimos que no existe

        //Sentencia sql para evitar la inyeccion de datos
        String sql = "select count(*) from productos where nombre = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, name);
        //Ejecucion de la consulta
        ResultSet rs = ps.executeQuery();

        //Si el resultado de la consulta es > 0 el producto ya existe
        boolean exists = false;
        if (rs.next()){
            exists = rs.getInt(1) > 0;
        }
        rs.close();
        ps.close();
        con.close();
        //Devolvemos si el producto existe o no
        return exists;

    }

//------------Getters & Setters-------------//

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
