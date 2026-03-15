-- ============================================================
-- PlayPDF - Script de creación de base de datos
-- Ejecutar en MySQL local antes de arrancar el backend
-- ============================================================

CREATE DATABASE IF NOT EXISTS playpdf_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE playpdf_db;

-- ============================================================
-- TABLA: centros
-- ============================================================
CREATE TABLE IF NOT EXISTS centros (
    id_centro    BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(200) NOT NULL,
    ciudad       VARCHAR(100) NOT NULL,
    direccion    VARCHAR(300) NOT NULL,
    latitud      DOUBLE       NOT NULL,
    longitud     DOUBLE       NOT NULL
);

-- ============================================================
-- TABLA: usuarios
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario      BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(150) NOT NULL,
    email           VARCHAR(200) NOT NULL UNIQUE,
    contrasena      VARCHAR(255) NOT NULL,
    rol             ENUM('alumno','profesor','administrador') NOT NULL DEFAULT 'alumno',
    edad            INT,
    estudios        VARCHAR(200),
    fecha_registro  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLA: asignaturas
-- ============================================================
CREATE TABLE IF NOT EXISTS asignaturas (
    id_asignatura   BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(200) NOT NULL,
    descripcion     TEXT,
    id_profesor     BIGINT,
    id_centro       BIGINT,
    CONSTRAINT fk_asig_profesor FOREIGN KEY (id_profesor) REFERENCES usuarios(id_usuario) ON DELETE SET NULL,
    CONSTRAINT fk_asig_centro   FOREIGN KEY (id_centro)   REFERENCES centros(id_centro)   ON DELETE SET NULL
);

-- ============================================================
-- TABLA: temas
-- ============================================================
CREATE TABLE IF NOT EXISTS temas (
    id_tema              BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo               VARCHAR(300) NOT NULL,
    descripcion          TEXT,
    nombre_archivo_pdf   VARCHAR(300),
    ruta_archivo_pdf     VARCHAR(500),
    fecha_subida         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_asignatura        BIGINT NOT NULL,
    CONSTRAINT fk_tema_asig FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id_asignatura) ON DELETE CASCADE
);

-- ============================================================
-- TABLA: preguntas
-- ============================================================
CREATE TABLE IF NOT EXISTS preguntas (
    id_pregunta  BIGINT AUTO_INCREMENT PRIMARY KEY,
    enunciado    TEXT NOT NULL,
    tipo         ENUM('quiz','puzzle') NOT NULL DEFAULT 'quiz',
    id_tema      BIGINT NOT NULL,
    CONSTRAINT fk_preg_tema FOREIGN KEY (id_tema) REFERENCES temas(id_tema) ON DELETE CASCADE
);

-- ============================================================
-- TABLA: respuestas
-- ============================================================
CREATE TABLE IF NOT EXISTS respuestas (
    id_respuesta  BIGINT AUTO_INCREMENT PRIMARY KEY,
    texto         TEXT NOT NULL,
    es_correcta   BOOLEAN NOT NULL DEFAULT FALSE,
    id_pregunta   BIGINT NOT NULL,
    CONSTRAINT fk_resp_preg FOREIGN KEY (id_pregunta) REFERENCES preguntas(id_pregunta) ON DELETE CASCADE
);

-- ============================================================
-- TABLA: estadisticas
-- ============================================================
CREATE TABLE IF NOT EXISTS estadisticas (
    id_estadistica      BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario          BIGINT NOT NULL,
    id_asignatura       BIGINT,
    total_partidas      INT DEFAULT 0,
    total_aciertos      INT DEFAULT 0,
    total_preguntas     INT DEFAULT 0,
    racha_actual        INT DEFAULT 0,
    fecha_ultimo_acceso TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_est_usuario   FOREIGN KEY (id_usuario)    REFERENCES usuarios(id_usuario)       ON DELETE CASCADE,
    CONSTRAINT fk_est_asig      FOREIGN KEY (id_asignatura) REFERENCES asignaturas(id_asignatura) ON DELETE SET NULL,
    UNIQUE KEY uq_usuario_asig (id_usuario, id_asignatura)
);

-- ============================================================
-- DATOS DE EJEMPLO
-- ============================================================

-- Centros
INSERT INTO centros (nombre, ciudad, direccion, latitud, longitud) VALUES
('IES Lucía de Medrano', 'Salamanca', 'C/ Lucía de Medrano, 1', 40.9629, -5.6631),
('IES Bachiller Sabuco', 'Albacete', 'C/ Hermanos Valdés, 22', 38.9944, -1.8568),
('IES Rey Pastor', 'Madrid', 'Av. de la Albufera, 150', 40.3906, -3.6596);

-- Usuario administrador (contraseña: admin123)
INSERT INTO usuarios (nombre, apellidos, email, contrasena, rol, edad, estudios) VALUES
('Admin', 'PlayPDF', 'admin@playpdf.com', '$2a$10$N.zmdr9zkoa05H2bTEsqkuPJyq5gvLiIAX8P3j6YOEuuGJBG7Iiyu', 'administrador', 30, 'Administración');

-- Usuario profesor de ejemplo (contraseña: prof123)
INSERT INTO usuarios (nombre, apellidos, email, contrasena, rol, edad, estudios) VALUES
('Carlos', 'García López', 'profesor@playpdf.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQNH8T7K7gFBtWWTkRa8ZSHYzJ7Kze', 'profesor', 35, 'Ingeniería Informática');

-- Usuario alumno de ejemplo (contraseña: alumno123)
INSERT INTO usuarios (nombre, apellidos, email, contrasena, rol, edad, estudios) VALUES
('María', 'Martínez Sanz', 'alumno@playpdf.com', '$2a$10$GRiChBX./OXG0hmEpRe8t.OdgABgmss0q1oQMPa0jQhAKQ4fy1b66', 'alumno', 20, 'DAW');
