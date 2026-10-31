package com.rastrenergy;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import org.json.JSONObject;

public class PerfilConsumoUDPServer {
    public static void main(String[] args) {
        
        int port = Integer.parseInt(getEnvOrDefault("UDP_PORT", "5001"));
        String dbHost = getEnvOrDefault("DB_HOST", "localhost");
        String dbPort = getEnvOrDefault("DB_PORT", "5432");
        String dbName = getEnvOrDefault("DB_NAME", "rastrenergy_db");
        String dbUser = getEnvOrDefault("DB_USER", "postgres");
        String dbPassword = getEnvOrDefault("DB_PASSWORD", "jdgc110346#rg");
        String dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

        System.out.println("==========================================================");
        System.out.println("  RASTRENERGY - SERVICIO DE PERFIL DE CONSUMO (UDP)       ");
        System.out.println("==========================================================");
        System.out.println("Base de datos configurada en: " + dbUrl);
        System.out.println("Usuario: " + dbUser);

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("\n[Rastrenergy] Servidor UDP listo en puerto " + port + ". Esperando datagramas...");
            byte[] buffer = new byte[2048];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String payload = new String(packet.getData(), 0, packet.getLength());
                JSONObject json = new JSONObject(payload);

                try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                    String query = "INSERT INTO lecturas_consumo (id_medidor, id_cliente, lectura_kwh) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, json.getString("id_medidor"));
                    stmt.setString(2, json.getString("id_cliente"));
                    stmt.setDouble(3, json.getDouble("lectura_kWh"));
                    stmt.executeUpdate();

                    System.out.println("[UDP - Persistido en BD] Medidor: " + json.getString("id_medidor") +
                                       " | Cliente: " + json.getString("id_cliente") +
                                       " | Lectura: " + json.getDouble("lectura_kWh") + " kWh promedio en un dia");
                } catch (Exception dbEx) {
                    System.err.println("[Error Base de Datos]: " + dbEx.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}