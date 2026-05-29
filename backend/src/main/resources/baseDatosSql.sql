CREATE DATABASE IF NOT EXISTS playpdf;
USE playpdf;

CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    edad INT,
    estudios VARCHAR(100),
    rol ENUM('ALUMNO', 'PROFESOR', 'ADMIN') NOT NULL,
    fecha_registro DATETIME
);

CREATE TABLE centro (
    id_centro BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    ciudad VARCHAR(100),
    direccion VARCHAR(200),
    latitud DECIMAL(9,6),
    longitud DECIMAL(9,6),
    codigo_acceso VARCHAR(20) UNIQUE
);

CREATE TABLE usuario_centro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_centro BIGINT NOT NULL,
    UNIQUE (id_usuario, id_centro),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_centro) REFERENCES centro(id_centro)
);

CREATE TABLE asignatura (
    id_asignatura BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_centro BIGINT NOT NULL,
    id_profesor BIGINT NOT NULL,
    FOREIGN KEY (id_centro) REFERENCES centro(id_centro),
    FOREIGN KEY (id_profesor) REFERENCES usuario(id_usuario)
);

CREATE TABLE tema (
    id_tema BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    nombre_archivo_pdf VARCHAR(255),
    ruta_archivo_pdf VARCHAR(500),
    fecha_subida DATETIME,
    id_asignatura BIGINT NOT NULL,
    FOREIGN KEY (id_asignatura) REFERENCES asignatura(id_asignatura)
);

CREATE TABLE preguntas (
    id_pregunta BIGINT AUTO_INCREMENT PRIMARY KEY,
    enunciado TEXT NOT NULL,
    tipo ENUM('quiz', 'puzzle') NOT NULL,
    id_tema BIGINT NOT NULL,
    FOREIGN KEY (id_tema) REFERENCES tema(id_tema)
);

CREATE TABLE respuestas (
    id_respuesta BIGINT AUTO_INCREMENT PRIMARY KEY,
    texto TEXT NOT NULL,
    es_correcta TINYINT(1) NOT NULL,
    id_pregunta BIGINT NOT NULL,
    FOREIGN KEY (id_pregunta) REFERENCES preguntas(id_pregunta)
);

CREATE TABLE estadistica (
    id_estadistica BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_asignatura BIGINT NOT NULL,
    total_partidas INT DEFAULT 0,
    total_aciertos INT DEFAULT 0,
    total_preguntas INT DEFAULT 0,
    racha_actual INT DEFAULT 0,
    fecha_ultimo_acceso DATETIME,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_asignatura) REFERENCES asignatura(id_asignatura)
);
