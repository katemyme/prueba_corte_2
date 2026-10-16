-- Crear la base de de datos llamada papeleria_campus segun lo indicado en el pdf
CREATE DATABASE papeleria_campus
-- antes de creer las tablas definimos el esquema donde se guardaran las tablas
CREATE SCHEMA catalogos;
--creacion de la tabla productos
CREATE TABLE catalogos.producto (
id_productos SERIAL PRIMARY KEY,
codigo      VARCHAR(10)   NOT NULL UNIQUE,
nombre      VARCHAR(80)   NOT NULL,
categoria   VARCHAR(20)   NOT NULL,
precio      NUMERIC(10,2) NOT NULL,
stock       INTEGER       NOT NULL,
estado SMALLINT NOT NULL DEFAULT 1,
CONSTRAINT chk_producto_codigo
    CHECK (codigo = UPPER(BTRIM(codigo)) AND char_length(codigo) BETWEEN 1 AND 10),
CONSTRAINT chk_producto_nombre
    CHECK (char_length(BTRIM(nombre)) BETWEEN 3 AND 80),
CONSTRAINT chk_producto_categoria
    CHECK (categoria IN ('Papelería', 'Tecnología', 'Accesorios')),
CONSTRAINT chk_producto_precio
    CHECK (precio > 0),
CONSTRAINT chk_producto_stock
    CHECK (stock >= 0),
CONSTRAINT chk_producto_estado
    CHECK (estado IN (0,1))
);
