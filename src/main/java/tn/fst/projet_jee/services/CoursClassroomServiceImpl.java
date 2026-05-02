package tn.fst.projet_jee.services;



import tn.fst.projet_jee.entities.*;
import tn.fst.projet_jee.repositories.*;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation de l'interface ICoursClassroomService.
 *
 * @Service : marque cette classe comme un composant de couche service Spring.
 * Spring l'instancie automatiquement et la rend injectable partout via @Autowired.
 */
@Service
public class CoursClassroomServiceImpl implements ICoursClassroomService {

    /**
     * Injection des repositories via @Autowired.
     * Spring injecte automatiquement les beans correspondants.
     */
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private CoursClassroomRepository coursClassroomRepository;

    // =====================================================================
    // PARTIE II - a) Ajouter un utilisateur
    // =====================================================================

    /**
     * Persiste un utilisateur en base de données.
     * save() de JpaRepository : fait un INSERT si l'ID est null, UPDATE sinon.
     *
     * @param utilisateur l'utilisateur à ajouter
     * @return l'utilisateur avec son ID auto-généré
     */
    @Override
    public Utilisateur ajouterUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    // =====================================================================
    // PARTIE II - b) Ajouter une classe
    // =====================================================================

    /**
     * Persiste une classe en base de données.
     *
     * @param c la classe à ajouter
     * @return la classe avec son codeClasse auto-généré
     */
    @Override
    public Classe ajouterClasse(Classe c) {
        return classeRepository.save(c);
    }

    // =====================================================================
    // PARTIE II - c) Ajouter un CoursClassroom et l'affecter à une classe
    // =====================================================================

    /**
     * Ajoute un CoursClassroom et l'affecte à une classe existante.
     *
     * Étapes :
     * 1. Récupérer la Classe par son codeClasse (orElseThrow si non trouvée)
     * 2. Associer la classe au cours (cc.setClasse(classe))
     * 3. Sauvegarder le cours → la FK "code_classe" sera renseignée en base
     *
     * @param cc         le cours à ajouter
     * @param codeClasse l'ID de la classe à laquelle l'affecter
     * @return le cours sauvegardé avec sa classe
     */
    @Override
    public CoursClassroom ajouterCoursClassroom(CoursClassroom cc, Integer codeClasse) {
        // Récupérer la classe correspondante ou lever une exception si inexistante
        Classe classe = classeRepository.findById(codeClasse)
                .orElseThrow(() -> new RuntimeException("Classe introuvable avec le code : " + codeClasse));

        // Associer le cours à la classe
        cc.setClasse(classe);

        // Sauvegarder et retourner le cours persisté
        return coursClassroomRepository.save(cc);
    }

    // =====================================================================
    // PARTIE II - d) Affecter un utilisateur à une classe
    // =====================================================================

    /**
     * Affecte un utilisateur à une classe (relation unidirectionnelle ManyToOne).
     *
     * Étapes :
     * 1. Charger l'Utilisateur et la Classe depuis la base
     * 2. Définir l'utilisateur de la classe (setUtilisateur)
     * 3. Sauvegarder la classe (la FK id_utilisateur sera mise à jour)
     *
     * Note : Selon le diagramme, la relation est 1 Utilisateur -> * Classe (unidirectionnelle)
     * Donc une classe a UN seul utilisateur, pas une liste.
     *
     * @param idUtilisateur l'ID de l'utilisateur à affecter
     * @param codeClasse    l'ID de la classe cible
     */
    @Override
    @Transactional
    public void affecterUtilisateurClasse(Integer idUtilisateur, Integer codeClasse) {
        // Charger l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + idUtilisateur));

        // Charger la classe
        Classe classe = classeRepository.findById(codeClasse)
                .orElseThrow(() -> new RuntimeException("Classe introuvable : " + codeClasse));

        // Affecter l'utilisateur à la classe (relation ManyToOne)
        classe.setUtilisateur(utilisateur);

        // Sauvegarder → met à jour la FK "id_utilisateur" dans la table "classe"
        classeRepository.save(classe);
    }

    // =====================================================================
    // PARTIE II - e) Nombre d'utilisateurs par niveau
    // =====================================================================

    /**
     * Retourne le nombre d'utilisateurs dans les classes d'un niveau donné.
     * Délègue la requête JPQL au repository.
     *
     * @param nv le niveau filtré
     * @return le nombre d'utilisateurs distincts
     */
    @Override
    public Integer nbUtilisateursParNiveau(Niveau nv) {
        return classeRepository.countUtilisateursByNiveau(nv);
    }

    // =====================================================================
    // PARTIE II - f) Désaffecter un CoursClassroom de sa classe
    // =====================================================================

    /**
     * Désaffecte un cours de sa classe (met la FK à null).
     *
     * @Transactional : nécessaire car on utilise @Modifying dans le repository.
     * Sans @Transactional, Hibernate lève une TransactionRequiredException.
     *
     * @param idCours l'ID du cours à désaffecter
     */
    @Override
    @Transactional
    public void desaffecterCoursClassroomClasse(Integer idCours) {
        coursClassroomRepository.desaffecterDeLaClasse(idCours);
    }

    // =====================================================================
    // PARTIE II - g) Archiver tous les cours (Spring Scheduler - BONUS)
    // =====================================================================

    /**
     * Archive tous les CoursClassroom existants (archive = true).
     *
     * @Scheduled(fixedRate = 60000) : se déclenche toutes les 60 secondes
     * après la fin de la dernière exécution.
     * → fixedRate = délai en millisecondes (60000ms = 60s)
     *
     * Pour activer le scheduling, @EnableScheduling doit être présent
     * sur la classe principale (ProjetSpringApplication).
     *
     * @Transactional : requis pour les opérations @Modifying.
     */
    @Override
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void archiverCoursClassrooms() {
        System.out.println("[SCHEDULER] Archivage de tous les cours en cours...");
        coursClassroomRepository.archiverTous();
        System.out.println("[SCHEDULER] Archivage terminé avec succès.");
    }

    // =====================================================================
    // PARTIE II - h) Nombre d'heures par spécialité et niveau
    // =====================================================================

    /**
     * Calcule le total des heures pour une spécialité et un niveau donnés.
     *
     * @param sp la spécialité (ex: AGRICULTURE)
     * @param nv le niveau (ex: QUATRIEME)
     * @return la somme des nbHeures, ou 0 si aucun cours trouvé
     */
    @Override
    public Integer nbHeuresParSpecEtNiv(Specialite sp, Niveau nv) {
        Integer result = coursClassroomRepository.sumNbHeuresBySpecialiteAndNiveau(sp, nv);
        // Retourner 0 au lieu de null si aucun cours ne correspond
        return (result != null) ? result : 0;
    }
}