# Programación y Plataformas Web

# **Spring Boot – Autenticación y Autorización con JWT: Seguridad y Control de Acceso**

<div align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="95">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg" width="95">
</div>

## **Práctica 11 (Spring Boot): Autenticación JWT, Autorización por Roles y Protección de Endpoints**

### **Autor**

**Adrian Lazo**

📧 [blazoc@ups.edu.ec](mailto:blazoc@ups.edu.ec)

💻 GitHub: [scomygod](https://github.com/scomygod)

---

# **Evidencias de Consumo de Endpoints**

## **1. Registro de Usuario (POST /auth/register)**
Creación de un nuevo usuario sin requerir token de autenticación.

![Registro de usuario](assets/capture11.2.png)

---

## **2. Login de Usuario (POST /auth/login)**
Autenticación de usuario y generación de token JWT.

![Login de usuario](assets/capture11.1.png)

---

## **3. Listar Usuarios (GET /api/users)**
Consulta de usuarios sin token, permitida por configuración de seguridad.

![Listar usuarios sin token](assets/capture11.3.png)