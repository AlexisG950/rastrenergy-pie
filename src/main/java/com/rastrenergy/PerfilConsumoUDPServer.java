package com.rastrenergy;

import org.json.JSONObject;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class PerfilConsumoUDPServer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================================");
        System.out.println("  RASTRENERGY - SERVICIO DE PERFIL DE CONSUMO (UDP)       ");
        System.out.println("==========================================================");
        System.out.print("Puerto de escucha UDP (ej. 5000): ");
        int port = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Host de PostgreSQL (ej. localhost): ");
        String dbHost = scanner.nextLine().trim();

        System.out.print("Puerto de PostgreSQL (ej. 5432): ");
        String dbPort = scanner.nextLine().trim();

        System.out.print("Nombre de base de datos (ej. rastrenergy_db): ");
        String dbName = scanner.nextLine().trim();

        System.out.print("Usuario de PostgreSQL: ");
        String dbUser = scanner.nextLine().trim();

        System.out.print("Contrasena de PostgreSQL: ");
        String dbPassword = scanner.nextLine().trim();

        String dbUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

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
                                       " | Lectura: " + json.getDouble("lectura_kWh") + " kWh");
                } catch (Exception dbEx) {
                    System.err.println("[Error Base de Datos]: " + dbEx.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
