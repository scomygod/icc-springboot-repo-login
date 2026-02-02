# =========================
# Stage 1: Build
# =========================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiar Gradle wrapper y archivos de configuración
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./

# Dar permisos de ejecución al wrapper
RUN chmod +x gradlew

# Cachear dependencias
RUN ./gradlew dependencies --no-daemon

# Copiar el código fuente
COPY src ./src

# Construir el JAR ejecutable
RUN ./gradlew bootJar --no-daemon

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/build/libs/*.jar app.jar

# Puerto estándar para contenedores / Render
EXPOSE 8080

# Ejecutar la aplicación
ENTRYPOINT ["java","-jar","app.jar"]