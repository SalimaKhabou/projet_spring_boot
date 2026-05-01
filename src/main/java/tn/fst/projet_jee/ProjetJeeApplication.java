package tn.fst.projet_jee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;




import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principale de l'application Spring Boot.
 *
 * @SpringBootApplication : annotation composée qui active :
 *   - @Configuration      : cette classe est une source de beans Spring
 *   - @EnableAutoConfiguration : active la configuration automatique de Spring Boot
 *   - @ComponentScan      : scanne le package et ses sous-packages pour trouver les composants
 *
 * @EnableScheduling : active le support des tâches planifiées (@Scheduled).
 *   OBLIGATOIRE pour que la méthode archiverCoursClassrooms() se déclenche
 *   toutes les 60 secondes dans CoursClassroomServiceImpl.
 */
@SpringBootApplication
@EnableScheduling
public class ProjetJeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetJeeApplication.class, args);
        System.out.println("✅ Application démarrée avec succès !");
        //System.out.println("📖 Swagger UI disponible : http://localhost:8089/swagger-ui/index.html");
    }
}