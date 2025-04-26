package models;

import com.sun.tools.javac.Main;
import jdk.swing.interop.SwingInterOpUtils;

import java.io.*;
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
public static Product found;

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
        DateTimeFormatter ciro = DateTimeFormatter.ofPattern("yyyy-MM-dd|HH:mm:ss");
        String dateTime = LocalDateTime.now().format(ciro);
        String fileName = "products_" + dateTime + ".txt";

        try{
            Scanner sc = new Scanner(System.in);
            System.out.println("Introduce la ruta donde se debe guardar el archivo");
            String path = sc.nextLine();
            File file = new File(path + "\\" + fileName);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            for (Product p : products){
                bw.write(p.getName() + "|" + p.getDescription() +
                        "|" + p.getQuantity() + "|" + p.getPrice());
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
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Ruta completa del archivo a importar: ");
            String path = sc.nextLine();
            File file = new File(path);

            if (!file.exists()) {
                System.out.println("El archivo no existe");
                return;
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");

                String name = data[0];
                String description = data[1];
                double price = Double.parseDouble(data[2]);
                int quantity = Integer.parseInt(data[3]);

                Product neew = new Product(name, description, price, quantity);

                if (!productExists(products, name)) {
                    products.add(neew);
                    GestionDatos.insertProductDB(neew);
                }
            }

            br.close();
            System.out.println("Productos importados correctamente");
        }catch (Exception e){
            System.out.println("Algo salió mal al importar los productos");
            e.printStackTrace();
        }
    }
    private static boolean productExists(ArrayList<Product> products, String name){
        for (Product p : products){
            if (p.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    //resto metodos-Funciones(ejecutables por gerentes y empleados)
    public static void addProduct(ArrayList<Product> products, Scanner sc) {
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
        if (products.isEmpty()){
            System.out.println("no hay productos");
        }else {
            for (Product p : products){
                System.out.println(p);
            }
        }
    }
    public static Product searchProduct(ArrayList<Product> products, String name){
        for (Product p : products){
            if (p.getName().equalsIgnoreCase(name)){
                return p;
            }
        }
        return null;
    }
    public static void sellProduct(ArrayList<Product> products, Scanner sc) {
        System.out.println("nombre del producto a vender: ");
        String name = sc.nextLine();

        found = searchProduct(products, name);

        if (found != null){
            System.out.println("cantidad a vernder: ");
            int sellQuantity = sc.nextInt();
            sc.nextLine();

            if (sellQuantity > 0 && found.getQuantity() >= sellQuantity){
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
        System.out.println("Nombre del producto a reponer: ");
        String name = sc.nextLine();

        found = searchProduct(products, name);

        if (found != null){
            System.out.println("Cantidad a  reponer: ");
            int restockQuantity = sc.nextInt();
            sc.nextLine();

            if (restockQuantity > 0){
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
        System.out.println("Nombredel producto a modificar: ");
        String name = sc.nextLine();

        found = searchProduct(products, name);

        if (found != null){
            System.out.println("Nueva descripcion:");
            String description = sc.nextLine();
            System.out.println("Nuevo precio:");
            double price = sc.nextDouble();
            sc.nextLine();
            System.out.println("Nueva cantidad:");
            int quantity = sc.nextInt();
            sc.nextLine();

            found.setDescription(description);
            found.setPrice(price);
            found.setQuantity(quantity);

            GestionDatos.updateProductDB(found);
            System.out.println("El producto se modifico de manera exitosa");
        }else {
            System.out.println("El producto " + name + " no se encontró");
        }
    }
    public static void deleteProduct(ArrayList<Product> products, Scanner sc) {
        System.out.println("Nombre del producto a eliminar: ");
        String name = sc.nextLine();
        found = searchProduct(products,name);

        if (found != null){
            products.remove(found);
            GestionDatos.deleteProductDB(found.getName());
            System.out.println("El producto " + name + " se eliminó con exito");
        }else {
            System.out.println("No se encontró el producto " + name);
        }
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
