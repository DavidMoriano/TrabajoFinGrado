# ---- Fase 1: compilar el JAR ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Descarga dependencias antes de copiar el código (caché de capas)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# ---- Fase 2: imagen final solo con el JAR ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/playpdf-backend-1.0.0.jar app.jar
# Directorio donde Spring guardará los PDFs subidos
RUN mkdir -p /app/pdfs-subidos
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
