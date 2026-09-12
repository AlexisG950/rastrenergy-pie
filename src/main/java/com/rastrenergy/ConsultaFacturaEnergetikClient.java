package com.rastrenergy;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ConsultaFacturaEnergetikClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================================");
        System.out.println("   RASTRENERGY - SERVICIO 3: CONSULTA FACTURACION (TCP)   ");
        System.out.println("==========================================================");
        System.out.print("Host de Energetik (ej. localhost): ");
        String host = scanner.nextLine().trim();

        System.out.print("Puerto TCP de Energetik (ej. 5003): ");
        int port = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("ID Cliente (ej. CLI-88410): ");
        String idCliente = scanner.nextLine().trim();

        System.out.print("Periodo (ej. 08/2026): ");
        String periodo = scanner.nextLine().trim();

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            JSONObject request = new JSONObject();
            request.put("tipo_mensaje", "FACTURA_REQUEST");
            request.put("id_cliente", idCliente);
            request.put("periodo", periodo);

            out.println(request.toString());
            String response = in.readLine();

            System.out.println("\n[TCP - Respuesta recibida de Energetik]:");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}