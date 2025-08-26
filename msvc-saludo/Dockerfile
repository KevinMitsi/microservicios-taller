# =======================
# Etapa 1: Construcción (Builder)
# =======================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar solo el pom.xml primero (para cachear dependencias)
COPY pom.xml .

RUN mvn dependency:go-offline -B

# Copiar el código fuente

COPY src ./src

# Compilar el proyecto y generar el JAR
RUN mvn clean package -DskipTests


# =======================
# Etapa 2: Imagen final (Runtime)
# =======================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el jar desde el builder
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto 80
EXPOSE 80

# Ejecutar Spring Boot en el puerto 80
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=80"]
