# =======================
# Etapa 1: Construcción (Builder)
# =======================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiamos únicamente los archivos de configuración de dependencias primero
COPY pom.xml .

# Descargamos las dependencias (esto se cachea si no cambia pom.xml)
RUN mvn dependency:go-offline -B

# Ahora copiamos el código fuente
COPY src ./src

# Compilamos y empaquetamos la aplicación (jar)
RUN mvn clean package -DskipTests

# =======================
# Etapa 2: Imagen final (Runtime)
# =======================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el jar generado desde la etapa builder
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto de Spring Boot
EXPOSE 8083

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]