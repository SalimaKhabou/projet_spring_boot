package tn.fst.projet_jee.services;

import tn.fst.projet_jee.entities.*;
import tn.fst.projet_jee.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation des services métier du TP.
 *
 * @Service : Spring instancie cette classe et la rend injectable via @Autowired.
 */
@Service
public class CoursClassroomServiceImpl implements ICoursClassroomService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private CoursClassroomRepository coursClassroomRepository;

    // =========================================================================
    // a) Ajouter un utilisateur
    // Signature : public Utilisateur ajouterUtilisateur(Utilisateur utilisateur)
    // =========================================================================

    /**
     * Persiste un Utilisateur en base de données.
     *
     * save() → INSERT si idUtilisateur est null (nouvel objet)
     *       → UPDATE si idUtilisateur existe déjà
     *
     * La table "utilisateur" contient maintenant :
     *   id_utilisateur, prenom, nom, password, code_classe (FK nullable)
     */
    @Override
    public Utilisateur ajouterUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    // =========================================================================
    // b) Ajouter une classe
    // Signature : public Classe ajouterClasse(Classe c)
    // =========================================================================

    /**
     * Persiste une Classe en base de données.
     *
     * La table "classe" contient :
     *   code_classe, titre, niveau (stocké en STRING grâce à @Enumerated)
     *
     * Note : la table "classe" NE contient PLUS de FK vers utilisateur.
     *         C'est "utilisateur" qui contient la FK "code_classe".
     */
    @Override
    public Classe ajouterClasse(Classe c) {
        return classeRepository.save(c);
    }

    // =========================================================================
    // c) Ajouter un CoursClassroom et l'affecter à une classe
    // Signature : public CoursClassroom ajouterCoursClassroom(CoursClassroom cc, Integer codeClasse)
    // =========================================================================

    /**
     * Ajoute un CoursClassroom et l'affecte à une Classe existante.
     *
     * Étapes :
     * 1. Charger la Classe par codeClasse (exception si inexistante)
     * 2. Associer la Classe au cours via cc.setClasse(classe)
     * 3. Sauvegarder → Hibernate renseigne la FK "code_classe" dans cours_classroom
     *
     * La relation CoursClassroom-Classe est BIDIRECTIONNELLE :
     * - CoursClassroom est côté propriétaire (possède la FK)
     * - Classe est côté inverse (mappedBy)
     * → On agit toujours sur le côté propriétaire (CoursClassroom) pour persister ✓
     */
    @Override
    public CoursClassroom ajouterCoursClassroom(CoursClassroom cc, Integer codeClasse) {
        Classe classe = classeRepository.findById(codeClasse)
                .orElseThrow(() -> new RuntimeException("Classe introuvable : " + codeClasse));
        cc.setClasse(classe);
        return coursClassroomRepository.save(cc);
    }

    // =========================================================================
    // d) Affecter un utilisateur à une classe
    // Signature : public void affecterUtilisateurClasse(Integer idUtilisateur, Integer codeClasse)
    // =========================================================================

    /**
     * Affecte un Utilisateur à une Classe.
     *
     *    utilisateur.setClasse(classe);
     *    utilisateurRepository.save(utilisateur);
     *    → Met à jour la FK "code_classe" dans la table "utilisateur"
     *    → Conforme au diagramme : Utilisateur(*) → Classe(1)
     *
     * Pourquoi @Transactional ?
     *    Utilisateur.classe est LAZY → le chargement nécessite une transaction active.
     *    Sans @Transactional, Hibernate lèverait LazyInitializationException.
     *
     * ─────────────────────────────────────────────────────────────────────
     * Résultat en base après les 2 appels de l'énoncé :
     *
     *   TABLE utilisateur :
     *   ┌──────────────┬───────┬────────┬──────────┬─────────────┐
     *   │idUtilisateur │prenom │nom     │password  │code_classe  │
     *   ├──────────────┼───────┼────────┼──────────┼─────────────┤
     *   │ 1            │Amna   │Ammar   │etudiant  │ 1  (4AG1)   │
     *   │ 2            │Ahmed  │Slama   │admin     │ 2  (5EM1)   │
     *   └──────────────┴───────┴────────┴──────────┴─────────────┘
     * ─────────────────────────────────────────────────────────────────────
     *
     * @param idUtilisateur ID de l'utilisateur à affecter
     * @param codeClasse    ID de la classe cible
     */
    @Override
    @Transactional
    public void affecterUtilisateurClasse(Integer idUtilisateur, Integer codeClasse) {
        // 1. Charger l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + idUtilisateur));

        // 2. Charger la classe
        Classe classe = classeRepository.findById(codeClasse)
                .orElseThrow(() -> new RuntimeException("Classe introuvable : " + codeClasse));

        // 3. Associer la classe à l'utilisateur (côté propriétaire = Utilisateur)
        //    → Hibernate mettra à jour code_classe dans la table "utilisateur"
        utilisateur.setClasse(classe);

        // 4. Sauvegarder l'utilisateur
        utilisateurRepository.save(utilisateur);
    }

    // =========================================================================
    // e) Nombre d'utilisateurs par niveau
    // Signature : public Integer nbUtilisateursParNiveau(Niveau nv)
    // =========================================================================

    /**
     * Compte les utilisateurs dans les classes d'un niveau donné.
     *
     * Délègue à la requête JPQL corrigée dans ClasseRepository :
     *   SELECT COUNT(u) FROM Utilisateur u
     *   WHERE u.classe IS NOT NULL AND u.classe.niveau = :nv
     *
     * Test question e) : nbUtilisateursParNiveau(QUATRIEME) → 1
     */
    @Override
    public Integer nbUtilisateursParNiveau(Niveau nv) {
        return classeRepository.countUtilisateursByNiveau(nv);
    }

    // =========================================================================
    // f) Désaffecter un CoursClassroom de sa classe
    // Signature : public void desaffecterCoursClassroomClasse(Integer idCours)
    // =========================================================================

    /**
     * Désaffecte un cours de sa classe en mettant la FK "code_classe" à NULL.
     *
     * @Modifying + @Transactional : requis pour toute requête UPDATE/DELETE
     * dans Spring Data JPA.
     *
     * SQL généré :
     *   UPDATE cours_classroom SET code_classe = NULL WHERE id_cours = ?
     *
     * Test : desaffecterCoursClassroomClasse(idCours de "Plantes")
     *         → code_classe = NULL dans cours_classroom pour "Plantes"
     */
    @Override
    @Transactional
    public void desaffecterCoursClassroomClasse(Integer idCours) {
        coursClassroomRepository.desaffecterDeLaClasse(idCours);
    }

    // =========================================================================
    // g) Archiver tous les cours — BONUS Spring Scheduler
    // Signature : public void archiverCoursClassrooms()
    // =========================================================================

    /**
     * Archive tous les CoursClassroom (archive = true).
     *
     * @Scheduled(fixedRate = 60000) : se déclenche automatiquement toutes
     *     les 60 000 ms = 60 secondes après le démarrage de l'application.
     *     PRÉREQUIS : @EnableScheduling sur ProjetJeeApplication.java ✓
     *
     * @Transactional : obligatoire pour la requête @Modifying dans le repo.
     *
     * Vérification : regarder les logs console → "[SCHEDULER] Archivage..."
     *                puis vérifier en MySQL : SELECT archive FROM cours_classroom;
     *                → tous à 1 (true)
     */
    @Override
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void archiverCoursClassrooms() {
        System.out.println("[SCHEDULER] Archivage de tous les cours en cours...");
        coursClassroomRepository.archiverTous();
        System.out.println("[SCHEDULER] Archivage terminé avec succès.");
    }

    // =========================================================================
    // h) Nombre d'heures par spécialité et niveau
    // Signature : public Integer nbHeuresParSpecEtNiv(Specialite sp, Niveau nv)
    // =========================================================================

    /**
     * Calcule la somme des heures pour une spécialité et un niveau donnés.
     *
     * Requête JPQL dans CoursClassroomRepository (INNER JOIN) :
     *   SELECT SUM(cc.nbHeures) FROM CoursClassroom cc
     *   JOIN cc.classe c
     *   WHERE cc.specialite = :sp AND c.niveau = :nv
     *
     *  Le INNER JOIN exclut les cours sans classe (code_classe = NULL).
     *     Donc si la question f) a été exécutée avant h) :
     *     "Plantes" (désaffecté, FK=NULL) → EXCLU du calcul.
     *
     * Test demandé : nbHeuresParSpecEtNiv(AGRICULTURE, QUATRIEME)
     *   → Avant f) : Plantes(25h) + Sciences Naturelles(40h) = 65h
     *   → Après f)  : Sciences Naturelles(40h) seulement  = 40h  ← réponse attendue
     *
     * @param sp spécialité (ex: Specialite.AGRICULTURE)
     * @param nv niveau     (ex: Niveau.QUATRIEME)
     * @return somme des heures, ou 0 si aucun cours trouvé
     */
    @Override
    public Integer nbHeuresParSpecEtNiv(Specialite sp, Niveau nv) {
        Integer result = coursClassroomRepository.sumNbHeuresBySpecialiteAndNiveau(sp, nv);
        return (result != null) ? result : 0;
    }
}