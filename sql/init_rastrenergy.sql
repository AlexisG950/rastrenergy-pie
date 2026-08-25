-- Tabla para registrar las lecturas UDP de los medidores inteligentes
CREATE TABLE IF NOT EXISTS lecturas_consumo (
    id SERIAL PRIMARY KEY,
    id_medidor VARCHAR(50) NOT NULL,
    id_cliente VARCHAR(50) NOT NULL,
    lectura_kwh NUMERIC(10, 2) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para almacenar precios horarios por franja y zona
CREATE TABLE IF NOT EXISTS precios_mercado (
    id SERIAL PRIMARY KEY,
    zona_geografica VARCHAR(50) NOT NULL,
    franja_horaria VARCHAR(50) NOT NULL,
    precio NUMERIC(10, 4) NOT NULL,
    moneda VARCHAR(10) DEFAULT 'USD'
);

-- Datos de prueba para precios spot
INSERT INTO precios_mercado (zona_geografica, franja_horaria, precio, moneda) VALUES
('CENTRAL', '00:00-08:00', 0.0800, 'USD'),
('CENTRAL', '08:00-18:00', 0.1600, 'USD'),
('CENTRAL', '18:00-23:00', 0.2200, 'USD'),
('ESTE', '00:00-08:00', 0.0750, 'USD'),
('ESTE', '08:00-18:00', 0.1500, 'USD'),
('ESTE', '18:00-23:00', 0.2100, 'USD');
