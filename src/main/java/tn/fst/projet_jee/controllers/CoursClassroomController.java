package tn.fst.projet_jee.controllers;

import tn.fst.projet_jee.entities.*;
import tn.fst.projet_jee.services.ICoursClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST exposant les endpoints de l'API.
 *
 * @RestController : combinaison de @Controller + @ResponseBody
 *   -> toutes les méthodes retournent directement du JSON (pas de vue JSP/Thymeleaf).
 *
 * @RequestMapping("/api") : préfixe commun à tous les endpoints de ce contrôleur.
 *   -> Ex: http://localhost:8089/api/ajouterUtilisateur
 *
 * Toutes les méthodes sont testables via Swagger UI :
 *   -> http://localhost:8089/swagger-ui/index.html
 * Ou via Postman en ciblant les URLs correspondantes.
 */
@RestController
@RequestMapping("/api")
public class CoursClassroomController {

    /**
     * Injection du service via l'interface (bonne pratique).
     * Spring injecte automatiquement CoursClassroomServiceImpl.
     */
    @Autowired
    private ICoursClassroomService coursClassroomService;

    // =====================================================================
    // PARTIE II - a) Ajouter un utilisateur
    // Signature imposée : public Utilisateur ajouterUtilisateur(Utilisateur utilisateur)
    // =====================================================================

    /**
     * Endpoint POST pour ajouter un utilisateur.
     *
     * URL : POST http://localhost:8089/api/ajouterUtilisateur
     * Body (JSON) :
     * {
     *   "prenom": "Amna",
     *   "nom": "Ammar",
     *   "password": "etudiant"
     * }
     *
     * @RequestBody : Spring désérialise automatiquement le JSON du body en objet Utilisateur.
     * @return l'utilisateur sauvegardé avec son ID généré
     */
    @PostMapping("/ajouterUtilisateur")
    public Utilisateur ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {
        return coursClassroomService.ajouterUtilisateur(utilisateur);
    }

    // =====================================================================
    // PARTIE II - b) Ajouter une classe
    // Signature imposée : public Classe ajouterClasse(Classe c)
    // =====================================================================

    /**
     * Endpoint POST pour ajouter une classe.
     *
     * URL : POST http://localhost:8089/api/ajouterClasse
     * Body (JSON) :
     * {
     *   "titre": "4AG1",
     *   "niveau": "QUATRIEME"
     * }
     *
     * @return la classe sauvegardée avec son codeClasse généré
     */
    @PostMapping("/ajouterClasse")
    public Classe ajouterClasse(@RequestBody Classe c) {
        return coursClassroomService.ajouterClasse(c);
    }

    // =====================================================================
    // PARTIE II - c) Ajouter un CoursClassroom et l'affecter à une classe
    // Signature imposée : public CoursClassroom ajouterCoursClassroom(CoursClassroom cc, Integer codeClasse)
    // =====================================================================

    /**
     * Endpoint POST pour ajouter un CoursClassroom et l'affecter à une classe.
     *
     * URL : POST http://localhost:8089/api/ajouterCoursClassroom?codeClasse=1
     * Body (JSON) :
     * {
     *   "specialite": "INFORMATIQUE",
     *   "nom": "Programmation C",
     *   "nbHeures": 42,
     *   "archive": false
     * }
     *
     * @RequestParam("codeClasse") : récupère le paramètre de l'URL (?codeClasse=...).
     * @return le CoursClassroom sauvegardé avec sa classe associée
     */
    @PostMapping("/ajouterCoursClassroom")
    public CoursClassroom ajouterCoursClassroom(
            @RequestBody CoursClassroom cc,
            @RequestParam("codeClasse") Integer codeClasse) {
        return coursClassroomService.ajouterCoursClassroom(cc, codeClasse);
    }

    // =====================================================================
    // PARTIE II - d) Affecter un utilisateur à une classe
    // Signature imposée : public void affecterUtilisateurClasse(Integer idUtilisateur, Integer codeClasse)
    // =====================================================================

    /**
     * Endpoint PUT pour affecter un utilisateur à une classe.
     *
     * URL : PUT http://localhost:8089/api/affecterUtilisateurClasse?idUtilisateur=1&codeClasse=1
     * Body : aucun (tous les paramètres sont dans l'URL)
     *
     * On utilise PUT car on modifie une ressource existante (la classe).
     */
    @PutMapping("/affecterUtilisateurClasse")
    public void affecterUtilisateurClasse(
            @RequestParam("idUtilisateur") Integer idUtilisateur,
            @RequestParam("codeClasse") Integer codeClasse) {
        coursClassroomService.affecterUtilisateurClasse(idUtilisateur, codeClasse);
    }

    // =====================================================================
    // PARTIE II - e) Nombre d'utilisateurs par niveau
    // Signature imposée : public Integer nbUtilisateursParNiveau(Niveau nv)
    // =====================================================================

    /**
     * Endpoint GET pour compter les utilisateurs d'un niveau donné.
     *
     * URL : GET http://localhost:8089/api/nbUtilisateursParNiveau?nv=QUATRIEME
     *
     * @return le nombre d'utilisateurs dans les classes du niveau spécifié
     */
    @GetMapping("/nbUtilisateursParNiveau")
    public Integer nbUtilisateursParNiveau(@RequestParam("nv") Niveau nv) {
        return coursClassroomService.nbUtilisateursParNiveau(nv);
    }

    // =====================================================================
    // PARTIE II - f) Désaffecter un CoursClassroom de sa classe
    // Signature imposée : public void desaffecterCoursClassroomClasse(Integer idCours)
    // =====================================================================

    /**
     * Endpoint PUT pour désaffecter un cours de sa classe.
     *
     * URL : PUT http://localhost:8089/api/desaffecterCoursClassroomClasse?idCours=2
     *
     * Après cet appel, le cours "Plantes" n'est plus rattaché à la classe "4AG1".
     * La FK "code_classe" devient NULL en base de données.
     */
    @PutMapping("/desaffecterCoursClassroomClasse")
    public void desaffecterCoursClassroomClasse(@RequestParam("idCours") Integer idCours) {
        coursClassroomService.desaffecterCoursClassroomClasse(idCours);
    }

    // =====================================================================
    // PARTIE II - g) Archiver tous les cours (déclenchée par le scheduler)
    // Exposée aussi en tant qu'endpoint pour test manuel
    // =====================================================================

    /**
     * Endpoint GET pour déclencher manuellement l'archivage de tous les cours.
     *
     * URL : GET http://localhost:8089/api/archiverCoursClassrooms
     *
     * Note : Cette méthode est également appelée automatiquement toutes les 60s
     * par le Spring Scheduler (@Scheduled dans CoursClassroomServiceImpl).
     */
    @GetMapping("/archiverCoursClassrooms")
    public void archiverCoursClassrooms() {
        coursClassroomService.archiverCoursClassrooms();
    }

    // =====================================================================
    // PARTIE II - h) Nombre d'heures par spécialité et niveau
    // Signature imposée : public Integer nbHeuresParSpecEtNiv(Specialite sp, Niveau nv)
    // =====================================================================

    /**
     * Endpoint GET pour calculer le total des heures par spécialité et niveau.
     *
     * URL : GET http://localhost:8089/api/nbHeuresParSpecEtNiv?sp=AGRICULTURE&nv=QUATRIEME
     *
     * Test demandé : AGRICULTURE + QUATRIEME
     * -> Si testé AVANT la désaffectation de "Plantes" (question f) :
     *    Plantes (25h) + Sciences Naturelles (40h) = 65 heures
     * -> Si testé APRÈS la désaffectation de "Plantes" (question f) :
     *    La jointure INNER JOIN exclut les cours sans classe (FK=null)
     *    Résultat = 40 heures (Sciences Naturelles uniquement)
     *
     * IMPORTANT : Dans l'ordre de l'énoncé, f) précède h), donc le résultat
     * attendu lors du test final est 40 heures.
     *
     * @return le total des heures correspondant aux critères
     */
    @GetMapping("/nbHeuresParSpecEtNiv")
    public Integer nbHeuresParSpecEtNiv(
            @RequestParam("sp") Specialite sp,
            @RequestParam("nv") Niveau nv) {
        return coursClassroomService.nbHeuresParSpecEtNiv(sp, nv);
    }
}