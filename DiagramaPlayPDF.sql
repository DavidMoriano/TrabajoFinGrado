CREATE DATABASE IF NOT EXISTS playpdf;
USE playpdf;

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    edad INT,
    estudios VARCHAR(100),
    rol ENUM('BASICO','PROFESOR','ADMIN') NOT NULL,
    codigo_acceso VARCHAR(50),
    fecha_registro DATE
);

CREATE TABLE centro (
    id_centro INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(200),
    ciudad VARCHAR(100),
    latitud DECIMAL(9,6),
    longitud DECIMAL(9,6)
);

CREATE TABLE asignatura (
    id_asignatura INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_centro INT NOT NULL,
    id_profesor INT NOT NULL,
    FOREIGN KEY (id_centro) REFERENCES centro(id_centro),
    FOREIGN KEY (id_profesor) REFERENCES usuario(id_usuario)
);

CREATE TABLE tema (
    id_tema INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT,
    archivo_pdf VARCHAR(255) NOT NULL,
    fecha_subida DATE,
    id_asignatura INT NOT NULL,
    FOREIGN KEY (id_asignatura) REFERENCES asignatura(id_asignatura)
);

CREATE TABLE juego (
    id_juego INT AUTO_INCREMENT PRIMARY KEY,
    tipo ENUM('TEST','PUZLE') NOT NULL,
    descripcion TEXT
);

CREATE TABLE pregunta (
    id_pregunta INT AUTO_INCREMENT PRIMARY KEY,
    enunciado TEXT NOT NULL,
    id_juego INT NOT NULL,
    id_tema INT NOT NULL,
    FOREIGN KEY (id_juego) REFERENCES juego(id_juego),
    FOREIGN KEY (id_tema) REFERENCES tema(id_tema)
);

CREATE TABLE respuesta (
    id_respuesta INT AUTO_INCREMENT PRIMARY KEY,
    texto TEXT NOT NULL,
    es_correcta TINYINT NOT NULL,
    id_pregunta INT NOT NULL,
    FOREIGN KEY (id_pregunta) REFERENCES pregunta(id_pregunta)
);

CREATE TABLE estadistica (
    id_estadistica INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_asignatura INT NOT NULL,
    fecha_acceso DATE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_asignatura) REFERENCES asignatura(id_asignatura)
);
