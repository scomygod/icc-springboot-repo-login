package ec.edu.ups.icc.fundamentos01.security.dtos;

import java.util.Set;

/**
 * DTO para respuestas de autenticación
 * 
 * Contiene el token JWT y los datos del usuario autenticado.
 * Usado como respuesta en:
 * - POST /api/auth/login
 * - POST /api/auth/register
 * 
 * Ejemplo de respuesta JSON:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "type": "Bearer",
 *   "userId": 1,
 *   "name": "Juan Pérez",
 *   "email": "juan@example.com",
 *   "roles": ["ROLE_USER"]
 * }
 */
public class AuthResponseDto {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String email;
    private Set<String> roles;

    // ============== CONSTRUCTORES ==============

    public AuthResponseDto() {
    }

    public AuthResponseDto(String token, Long userId, String name, String email, Set<String> roles) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    // ============== GETTERS Y SETTERS ==============

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}