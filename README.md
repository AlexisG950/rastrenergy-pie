# Rastrenergy - Precios Inteligentes de Energía (PIE)

## Integrantes
- Joaquin Onieva Zymanscki - 5678285
- Alex Rodrigo Gauto Cardozo - 5555875

## Descripción
Módulo correspondiente a la Organización 1 (Rastrenergy). Implementa:
1. **Servicio de Perfil de Consumo del Cliente (UDP en puerto 5000):** Recibe telemetría de medidores inteligentes y la almacena en PostgreSQL.
2. **Servicio de Consulta de Precios de Mercado (TCP en puerto 5001):** Expone precios spot horarios consultados desde PostgreSQL.

## Configuración y Base de Datos
1. Crear la base de datos en PostgreSQL y ejecutar el script `sql/init_rastrenergy.sql`.
2. Compilar el proyecto con Maven:
   ```bash
   mvn clean compile
