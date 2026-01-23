package ec.edu.ups.icc.fundamentos01.security.config;

import ec.edu.ups.icc.fundamentos01.security.models.RoleEntity;
import ec.edu.ups.icc.fundamentos01.security.models.RoleName;
import ec.edu.ups.icc.fundamentos01.security.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Inicializador de datos para la base de datos
 * Se ejecuta automáticamente al iniciar la aplicación
 */
@Configuration
public class DataInitializer {

    /**
     * Crea roles por defecto si no existen
     * 
     * @Bean CommandLineRunner se ejecuta después de que Spring Boot inicia
     * Recibe RoleRepository por inyección de dependencias
     */
    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            // Crear ROLE_USER si no existe
            if (roleRepository.findByName(RoleName.ROLE_USER).isEmpty()) {
                RoleEntity userRole = new RoleEntity(
                    RoleName.ROLE_USER, 
                    "Usuario estándar con permisos básicos"
                );
                roleRepository.save(userRole);
                System.out.println("✅ Rol ROLE_USER creado");
            }

            // Crear ROLE_ADMIN si no existe
            if (roleRepository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
                RoleEntity adminRole = new RoleEntity(
                    RoleName.ROLE_ADMIN, 
                    "Administrador con permisos completos"
                );
                roleRepository.save(adminRole);
                System.out.println("✅ Rol ROLE_ADMIN creado");
            }

            // Crear ROLE_MODERATOR si no existe
            if (roleRepository.findByName(RoleName.ROLE_MODERATOR).isEmpty()) {
                RoleEntity moderatorRole = new RoleEntity(
                    RoleName.ROLE_MODERATOR, 
                    "Moderador con permisos intermedios"
                );
                roleRepository.save(moderatorRole);
                System.out.println("✅ Rol ROLE_MODERATOR creado");
            }

            System.out.println("✅ Inicialización de roles completada");
        };
    }
}