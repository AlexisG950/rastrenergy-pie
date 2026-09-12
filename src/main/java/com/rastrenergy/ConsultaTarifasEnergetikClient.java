package com.rastrenergy;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ConsultaTarifasEnergetikClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================================");
        System.out.println("   RASTRENERGY - SERVICIO 2: CLIENTE CONSULTA TARIFAS TCP ");
        System.out.println("==========================================================");
        System.out.print("Host de Energetik (ej. localhost): ");
        String host = scanner.nextLine().trim();

        System.out.print("Puerto TCP de Energetik (ej. 5002): ");
        int port = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Categoria de cliente (ej. residencial): ");
        String categoria = scanner.nextLine().trim();

        System.out.print("Zona geografica (ej. CENTRAL): ");
        String zona = scanner.nextLine().trim();

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            JSONObject request = new JSONObject();
            request.put("tipo_mensaje", "TARIFAS_REQUEST");
            request.put("categoria_cliente", categoria);
            request.put("zona_geografica", zona);

            out.println(request.toString());
            String response = in.readLine();

            System.out.println("\n[TCP - Respuesta recibida de Energetik]:");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}