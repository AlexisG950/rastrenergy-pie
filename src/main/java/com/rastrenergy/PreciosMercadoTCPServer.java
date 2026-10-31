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

public class PreciosMercadoTCPServer {
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    public static void main(String[] args) {
        // Carga automática desde variables de entorno o valores por defecto
        int port = Integer.parseInt(getEnvOrDefault("TCP_PORT", "5001"));
        String dbHost = getEnvOrDefault("DB_HOST", "localhost");
        String dbPort = getEnvOrDefault("DB_PORT", "5432");
        String dbName = getEnvOrDefault("DB_NAME", "rastrenergy_db");
        dbUser = getEnvOrDefault("DB_USER", "postgres");
        dbPassword = getEnvOrDefault("DB_PASSWORD", "jdgc110346#rg");

        dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        System.out.println("==========================================================");
        System.out.println("  RASTRENERGY - CONSULTA DE PRECIOS DE MERCADO (TCP)      ");
        System.out.println("==========================================================");
        System.out.println("Base de datos configurada en: " + dbUrl);
        System.out.println("Usuario: " + dbUser);

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

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}