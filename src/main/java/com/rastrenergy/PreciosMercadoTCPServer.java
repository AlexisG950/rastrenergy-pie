package com.rastrenergy;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class PreciosMercadoTCPServer {
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================================");
        System.out.println("  RASTRENERGY - CONSULTA DE PRECIOS DE MERCADO (TCP)      ");
        System.out.println("==========================================================");
        System.out.print("Puerto de escucha TCP (ej. 5001): ");
        int port = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Host de PostgreSQL (ej. localhost): ");
        String dbHost = scanner.nextLine().trim();

        System.out.print("Puerto de PostgreSQL (ej. 5432): ");
        String dbPort = scanner.nextLine().trim();

        System.out.print("Nombre de base de datos (ej. rastrenergy_db): ");
        String dbName = scanner.nextLine().trim();

        System.out.print("Usuario de PostgreSQL: ");
        dbUser = scanner.nextLine().trim();

        System.out.print("Contrasena de PostgreSQL: ");
        dbPassword = scanner.nextLine().trim();

        dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("\n[Rastrenergy] Servidor TCP de Precios escuchando en puerto " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> procesarPeticion(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void procesarPeticion(Socket socket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String peticionStr = in.readLine();
            JSONObject peticion = new JSONObject(peticionStr);
            String zona = peticion.optString("zona_geografica", "CENTRAL");

            JSONObject response = new JSONObject();
            response.put("mercado", "Spot-Nacional");
            response.put("zona_geografica", zona);

            JSONArray listaPrecios = new JSONArray();

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                String sql = "SELECT franja_horaria, precio, moneda FROM precios_mercado WHERE zona_geografica = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, zona);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    JSONObject item = new JSONObject();
                    item.put("franja", rs.getString("franja_horaria"));
                    item.put("precio", rs.getDouble("precio"));
                    item.put("moneda", rs.getString("moneda"));
                    listaPrecios.put(item);
                }
            }
            response.put("precios", listaPrecios);
            out.println(response.toString());

        } catch (Exception e) {
            System.err.println("[Error procesando TCP]: " + e.getMessage());
        }
    }
}
